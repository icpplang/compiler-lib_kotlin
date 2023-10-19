package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.QualnameUsageBuilder
import io.github.landgrafhomyak.icpp.ast.builders.TemplateUsageBuilder
import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.SourceStream
import io.github.landgrafhomyak.icpp.parser.predicates.NameSymbolsPredicate
import io.github.landgrafhomyak.icpp.parser.predicates.SpaceSymbolsPredicate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object NameUsageParser {
    @JvmStatic
    suspend fun <CS : CollectedSubstring, P : Pos> parseIdentifier(
        stream: SourceStream<CS, P>,
    ): CS? {
        if (stream.isEnded)
            return null
        if (!NameSymbolsPredicate.checkFirstChar(stream.current))
            return null
        return stream.collect(NameSymbolsPredicate)
    }

    @JvmStatic
    fun checkIsQualnameStart(c: Char, consumeInvalidIdentifier: Boolean = false) =
        if (consumeInvalidIdentifier) NameSymbolsPredicate.check(c)
        else NameSymbolsPredicate.checkFirstChar(c)

    @OptIn(ExperimentalContracts::class)
    @JvmStatic
    private suspend inline fun <CS : CollectedSubstring, P : Pos> parseLevel(
        stream: SourceStream<CS, P>,
        builder: QualnameUsageBuilder<CS, P>,
        consumeSpaces: Boolean = true,
        consumeInvalidIdentifier: Boolean = false,
        onTrailingSpaces: (start: P, end: P) -> Unit
    ): Boolean {
        contract {
            callsInPlace(onTrailingSpaces, InvocationKind.AT_MOST_ONCE)
        }

        if (stream.isEnded)
            return true

        @Suppress("RemoveRedundantQualifierName")
        val name = NameUsageParser.parseIdentifier(stream)
        val badNameStart: P?
        val badNameEnd: P?

        if (name == null) {
            if (!consumeInvalidIdentifier)
                return true

            badNameStart = stream.pos
            stream.skip(NameSymbolsPredicate)
            badNameEnd = stream.pos
        } else {
            badNameStart = null
            badNameEnd = null
        }

        if (stream.isEnded)
            if (name != null) builder.addLevel(name) else builder.addBadLevel(badNameStart!!, badNameEnd!!)

        val spacesAfterNameStart: P?
        val spacesAfterNameEnd: P?
        if (SpaceSymbolsPredicate.check(stream.current)) {
            if (!consumeSpaces) {
                if (name != null) builder.addLevel(name) else builder.addBadLevel(badNameStart!!, badNameEnd!!)
                return false
            }

            spacesAfterNameStart = stream.pos
            stream.skip(SpaceSymbolsPredicate)
            spacesAfterNameEnd = stream.pos

            if (stream.isEnded) // qualname ended, spaces not an error
            {
                if (name != null) builder.addLevel(name) else builder.addBadLevel(badNameStart!!, badNameEnd!!)
                return false
            }

        } else {
            spacesAfterNameStart = null
            spacesAfterNameEnd = null
        }

        // stream end checked in spaces-consuming branch & no-spaces branch doesn't change stream

        // template opening brace
        if (stream.current != '$') {
            if (name != null) builder.addLevel(name) else builder.addBadLevel(badNameStart!!, badNameEnd!!)
            if (spacesAfterNameStart != null)
                onTrailingSpaces(spacesAfterNameStart, spacesAfterNameEnd!!)
            return false
        }

        val templateBuilder: TemplateUsageBuilder<CS, P> =
            if (name != null) builder.addTemplatedLevel(name)
            else builder.addBadTemplatedLevel(badNameStart!!, badNameEnd!!)

        if (spacesAfterNameStart != null)
            templateBuilder.spacesBeforeOpeningBrace(spacesAfterNameStart, spacesAfterNameEnd!!)

        val templateOpeningBraceStart = stream.pos
        if (stream.move() || stream.current != '<') {
            templateBuilder.unfinishedOpeningBrace(templateOpeningBraceStart, stream.pos)
        } else {
            stream.move()
            templateBuilder.openingBrace(templateOpeningBraceStart, stream.pos)
        }
        if (stream.isEnded)
            return false

        // template args parser
        TemplateUsageParser.parseTemplateArgs(stream, templateBuilder)

        // template closing brace
        if (stream.isEnded || stream.current != '>') {
            templateBuilder.missedClosingBrace(stream.pos)
            return false
        }
        val templateClosingBraceStart = stream.pos
        stream.move()
        templateBuilder.closingBrace(templateClosingBraceStart, stream.pos)

        // spaces after template
        if (!stream.isEnded && SpaceSymbolsPredicate.check(stream.current)) {
            if (!consumeSpaces)
                return false

            val spacesAfterTemplateStart = stream.pos
            stream.skip(SpaceSymbolsPredicate)
            onTrailingSpaces(spacesAfterTemplateStart, stream.pos)
        }

        return false
    }

    @JvmStatic
    @Suppress("RemoveRedundantQualifierName")
    suspend fun <CS : CollectedSubstring, P : Pos> parseQualname(
        stream: SourceStream<CS, P>,
        builder: QualnameUsageBuilder<CS, P>,
        consumeSpaces: Boolean = true,
        consumeInvalidIdentifier: Boolean = false
    ) {
        if (stream.isEnded || !NameUsageParser.checkIsQualnameStart(stream.current, consumeInvalidIdentifier)) {
            badParser("Qualname existence not pre-checked")
        }

        NameUsageParser.parseLevel(stream, builder, consumeSpaces, consumeInvalidIdentifier) { start, end ->
            if (stream.current != ':' && stream.current != '.')
                return // if no separator - qualname ended
            builder.spacesBetweenSeparatorAndName(start, end)
        }


        var isMember = false
        do {
            if (stream.isEnded) {
                return
            }

            // separator
            when (stream.current) {
                '.' -> {
                    val start = stream.pos
                    stream.move()
                    builder.memberAccessOperator(start, stream.pos)
                    isMember = true
                }

                ':' -> {
                    // todo: do we need second semicolon in separator?
                    val start = stream.pos
                    if (stream.move() || stream.current != ':') {
                        builder.unfinishedScopeResolutionOperator(start, stream.pos)
                    } else {
                        stream.move()
                        if (isMember)
                            builder.scopeOfMemberResolutionOperator(start, stream.pos)
                        else
                            builder.scopeResolutionOperator(start, stream.pos)
                    }
                    if (stream.isEnded)
                        return
                }

                else -> {
                    return
                }
            }

            // spaces after separator
            if (SpaceSymbolsPredicate.check(stream.current)) {
                if (!consumeSpaces) {
                    builder.nothingAfterSeparator(stream.pos)
                    return
                }

                val start = stream.pos
                stream.skip(SpaceSymbolsPredicate)
                builder.spacesBetweenSeparatorAndName(start, stream.pos)
            }

            // next level
            NameUsageParser
                .parseLevel(stream, builder, consumeSpaces, consumeInvalidIdentifier) { start, end ->
                    if (stream.current != ':' && stream.current != '.')
                        return // if no separator - qualname ended
                    builder.spacesBetweenSeparatorAndName(start, end)
                }
                .also { r -> if (!r) builder.nothingAfterSeparator(stream.pos) }
        } while (true)
    }

    const val MEMBER_ACCESS_OPERATOR = "."
    const val SCOPE_RESOLUTION_OPERATOR = "::"
}

