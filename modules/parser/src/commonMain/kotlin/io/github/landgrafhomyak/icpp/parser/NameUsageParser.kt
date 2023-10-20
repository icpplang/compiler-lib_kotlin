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
    /**
     * Consumes and returns valid name (e.g. not started from digit)
     */
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

    /**
     * Checks that current char can be first in qualname. Must be called before [NameUsageParser.parseQualname]
     *
     * @param consumeInvalidIdentifier Same as in [NameUsageParser.parseQualname]
     */
    @JvmStatic
    fun checkIsQualnameStart(c: Char, consumeInvalidIdentifier: Boolean = false) =
        if (consumeInvalidIdentifier) NameSymbolsPredicate.check(c)
        else NameSymbolsPredicate.checkFirstChar(c)

    /**
     * Parses level of qualname (name, spaces between name and template, template, spaces after template).
     *
     * @param superReturn Callback to return from [NameUsageParser.parseQualname], should be `{ return }` .
     *                    Called when met char that is not part of qualname.
     * @param ifNameNotFound Called when function doesn't found new level of qualname.
     */
    @OptIn(ExperimentalContracts::class)
    @JvmStatic
    private suspend inline fun <CS : CollectedSubstring, P : Pos> parseLevel(
        stream: SourceStream<CS, P>,
        builder: QualnameUsageBuilder<CS, P>,
        consumeSpaces: Boolean,
        consumeInvalidIdentifier: Boolean,
        superReturn: () -> Nothing,
        ifNameNotFound: () -> Nothing
    ) {
        contract {
            callsInPlace(superReturn, InvocationKind.AT_MOST_ONCE)
            callsInPlace(ifNameNotFound, InvocationKind.AT_MOST_ONCE)
        }

        @Suppress("RemoveRedundantQualifierName")
        if (stream.isEnded || !NameUsageParser.checkIsQualnameStart(stream.current, consumeInvalidIdentifier))
            ifNameNotFound()

        @Suppress("RemoveRedundantQualifierName")
        val name = NameUsageParser.parseIdentifier(stream)
        val badNameStart: P?
        val badNameEnd: P?

        if (name == null) {
            if (!consumeInvalidIdentifier)
                ifNameNotFound()

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
                return
            }

            spacesAfterNameStart = stream.pos
            stream.skip(SpaceSymbolsPredicate)
            spacesAfterNameEnd = stream.pos

            if (stream.isEnded) // qualname ended, spaces not an error
            {
                if (name != null) builder.addLevel(name) else builder.addBadLevel(badNameStart!!, badNameEnd!!)
                return
            }

        } else {
            spacesAfterNameStart = null
            spacesAfterNameEnd = null
        }

        // stream end checked in spaces-consuming branch & no-spaces branch doesn't change stream

        // template opening brace
        if (stream.current != '$') {
            if (name != null) builder.addLevel(name) else builder.addBadLevel(badNameStart!!, badNameEnd!!)
            if (spacesAfterNameStart != null) {
                if (stream.current != ':' && stream.current != '.')
                    superReturn() // if no separator - qualname ended
                builder.spacesBetweenSeparatorAndName(spacesAfterNameStart, spacesAfterNameEnd!!)
            }
            return
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
            return

        // template args parser
        TemplateUsageParser.parseTemplateArgs(stream, templateBuilder)

        // template closing brace
        if (stream.isEnded || stream.current != '>') {
            templateBuilder.missedSeparator(stream.pos)
            return
        }
        val templateClosingBraceStart = stream.pos
        stream.move()
        templateBuilder.closingBrace(templateClosingBraceStart, stream.pos)

        // spaces after template
        if (!stream.isEnded && SpaceSymbolsPredicate.check(stream.current)) {
            if (!consumeSpaces)
                return

            val spacesAfterTemplateStart = stream.pos
            stream.skip(SpaceSymbolsPredicate)
            if (stream.current != ':' && stream.current != '.')
                superReturn() // if no separator - qualname ended
            builder.spacesBetweenSeparatorAndName(spacesAfterTemplateStart, stream.pos)
        }

        return
    }

    /**
     * Function to parse qualnames in expressions.
     *
     * @param consumeSpaces Flag to consume [spaces][SpaceSymbolsPredicate] between qualname levels and separators
     *                      (error) and after qualname (not an error).
     *                      If not set, function will return after the first [space char][SpaceSymbolsPredicate].
     *                      Doesn't affect to spaces inside expressions inside template arguments.
     * @param consumeInvalidIdentifier Flag to parse invalid names (e.g. started from digits). This names reported
     *                                 as errors.
     */
    @JvmStatic
    @Suppress("RemoveRedundantQualifierName")
    suspend fun <CS : CollectedSubstring, P : Pos> parseQualname(
        stream: SourceStream<CS, P>,
        builder: QualnameUsageBuilder<CS, P>,
        consumeSpaces: Boolean = true,
        consumeInvalidIdentifier: Boolean = false
    ) {
        NameUsageParser
            .parseLevel(stream, builder, consumeSpaces, consumeInvalidIdentifier, { return })
            { badParser("Qualname existence not pre-checked") }


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
                .parseLevel(stream, builder, consumeSpaces, consumeInvalidIdentifier, { return }) {
                    builder.nothingAfterSeparator(stream.pos)
                    return
                }
        } while (true)
    }

    const val MEMBER_ACCESS_OPERATOR = "."
    const val SCOPE_RESOLUTION_OPERATOR = "::"
}

