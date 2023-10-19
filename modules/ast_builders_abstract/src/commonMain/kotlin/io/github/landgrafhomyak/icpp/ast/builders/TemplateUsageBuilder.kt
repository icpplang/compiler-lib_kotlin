package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos

interface TemplateUsageBuilder<CS : CollectedSubstring, P : Pos> {
    @HighlightOnly
    fun openingBrace(start: P, end: P)
    
    fun buildArg(): ExpressionBuilder<CS, P>

    @HighlightOnly
    fun argSeparator(start: P, end: P)

    fun endingBrace(@HighlightOnly start: P, @HighlightOnly end: P)
}