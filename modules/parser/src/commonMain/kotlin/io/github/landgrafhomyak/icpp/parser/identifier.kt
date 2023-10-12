package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.QualnameBuilder
import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.SourceStream
import io.github.landgrafhomyak.icpp.parser.predicates.IdentifierPredicate
import io.github.landgrafhomyak.icpp.parser.predicates.SpaceSymbolsPredicate

suspend fun <CS : CollectedSubstring, P : Pos> parseIdentifier(
    stream: SourceStream<CS, P>,
): CS? {
    if (stream.isEnded)
        return null
    if (!IdentifierPredicate.checkFirstChar(stream.current))
        return null
    return stream.collect(IdentifierPredicate)
}
