package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.QualnameBuilder
import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.SourceStream
import io.github.landgrafhomyak.icpp.parser.predicates.IdentifierPredicate
import io.github.landgrafhomyak.icpp.parser.predicates.SpaceSymbolsPredicate

object IdentifierParsers {
    @JvmStatic
    suspend fun <CS : CollectedSubstring, P : Pos> parseIdentifier(
        stream: SourceStream<CS, P>,
    ): CS? {
        if (stream.isEnded)
            return null
        if (!IdentifierPredicate.checkFirstChar(stream.current))
            return null
        return stream.collect(IdentifierPredicate)
    }

    @JvmStatic
    fun checkIsQualnameStart(c: Char, consumeInvalidIdentifier: Boolean = false) =
        if (consumeInvalidIdentifier) IdentifierPredicate.check(c)
        else IdentifierPredicate.checkFirstChar(c)

    @JvmStatic
    private suspend fun <CS : CollectedSubstring, P : Pos> consumeBadIdentifier(
        stream: SourceStream<CS, P>,
        builder: QualnameBuilder<CS, P>
    ) {
        val start = stream.pos
        stream.skip(IdentifierPredicate)
        builder.addBadLevel(start, stream.pos)
    }

    @JvmStatic
    suspend fun <CS : CollectedSubstring, P : Pos> parseQualname(
        stream: SourceStream<CS, P>,
        builder: QualnameBuilder<CS, P>,
        consumeSpaces: Boolean = true,
        consumeInvalidIdentifier: Boolean = false
    ) {
        @Suppress("RemoveRedundantQualifierName")
        if (stream.isEnded || !IdentifierParsers.checkIsQualnameStart(stream.current, consumeInvalidIdentifier)) {
            badParser("Qualname existence not pre-checked")
        }
        @Suppress("RemoveRedundantQualifierName")
        val firstLevel = IdentifierParsers.parseIdentifier(stream)
        if (firstLevel == null) {
            @Suppress("RemoveRedundantQualifierName")
            IdentifierParsers.consumeBadIdentifier(stream, builder)
        } else {
            builder.addLevel(firstLevel)
        }
        do {
            if (stream.isEnded)
                return

            // space before separator
            val spacesStart: P?
            val spacesEnd: P?
            if (SpaceSymbolsPredicate.check(stream.current)) {
                if (!consumeSpaces)
                    return

                spacesStart = stream.pos
                stream.skip(SpaceSymbolsPredicate)
                spacesEnd = stream.pos
                if (stream.isEnded)
                    return
            } else {
                spacesStart = null
                spacesEnd = null
            }

            // separator
            if (stream.current != ':')
                return
            if (spacesStart != null) {
                builder.spacesBetweenSeparatorAndName(spacesStart, spacesEnd!!)
            }
            // todo: do we need second semicolon in separator?
            val scopeResolutionOperatorStart = stream.pos
            if (stream.move() || stream.current != ':') {
                builder.unfinishedScopeResolutionOperator(scopeResolutionOperatorStart, stream.pos)
                return
            }
            builder.separator(scopeResolutionOperatorStart, stream.pos)

            if (stream.move()) {
                builder.nothingAfterSeparator(stream.pos)
                return
            }

            // spaces after separator
            if (SpaceSymbolsPredicate.check(stream.current)) {
                if (!consumeSpaces) {
                    builder.nothingAfterSeparator(stream.pos)
                    return
                }

                val spacesStart2 = stream.pos
                stream.skip(SpaceSymbolsPredicate)
                builder.spacesBetweenSeparatorAndName(spacesStart2, stream.pos)
            }

            // next identifier
            if (stream.isEnded || !IdentifierPredicate.check(stream.current)) {
                builder.nothingAfterSeparator(stream.pos)
                return
            }
            @Suppress("RemoveRedundantQualifierName")
            val nextLevel = IdentifierParsers.parseIdentifier(stream)
            if (nextLevel == null) {
                if (!consumeInvalidIdentifier) {
                    builder.nothingAfterSeparator(stream.pos)
                    return
                }
                @Suppress("RemoveRedundantQualifierName")
                IdentifierParsers.consumeBadIdentifier(stream, builder)
            } else {
                builder.addLevel(nextLevel)
            }
        } while (true)
    }
}