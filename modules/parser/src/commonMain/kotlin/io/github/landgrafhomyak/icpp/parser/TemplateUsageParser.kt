package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.TemplateUsageBuilder
import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.SourceStream
import io.github.landgrafhomyak.icpp.parser.predicates.NameSymbolsPredicate
import io.github.landgrafhomyak.icpp.parser.predicates.SpaceSymbolsMultilinePredicate

object TemplateUsageParser {
    @JvmStatic
    fun checkArgListStart(c: Char) = c == '$'

    @JvmStatic
    suspend fun <CS : CollectedSubstring, P : Pos> parseTemplateArgs(
        stream: SourceStream<CS, P>,
        builder: TemplateUsageBuilder<CS, P>,
        leadingSpacesStart: P,
        leadingSpacesEnd: P
    ) {
        builder.spacesBeforeOpeningBrace(leadingSpacesStart, leadingSpacesEnd)
        @Suppress("RemoveRedundantQualifierName")
        TemplateUsageParser.parseTemplateArgs(stream, builder)
    }

    @JvmStatic
    suspend fun <CS : CollectedSubstring, P : Pos> parseTemplateArgs(
        stream: SourceStream<CS, P>,
        builder: TemplateUsageBuilder<CS, P>,
    ) {
        if (stream.isEnded || stream.current != '$')
            badParser("Template arglist existence not pre-checked")

        val templateOpeningBraceStart = stream.pos
        if (stream.move() || stream.current != '<') {
            builder.unfinishedOpeningBrace(templateOpeningBraceStart, stream.pos)
        } else {
            stream.move()
            builder.openingBrace(templateOpeningBraceStart, stream.pos)
        }
        if (stream.isEnded) {
            builder.missedClosingBrace(stream.pos)
            return
        }

        while (true) {
            stream.skip(SpaceSymbolsMultilinePredicate)
            if (stream.isEnded) {
                builder.missedClosingBrace(stream.pos)
                return
            }
        }
    }
}