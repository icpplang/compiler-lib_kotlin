package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Error
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos

interface TemplateUsageBuilder<CS : CollectedSubstring, P : Pos> {
    @Error
    fun spacesBeforeOpeningBrace(start: P, end: P)

    @Error
    fun unfinishedOpeningBrace(start: P, end: P)

    @HighlightOnly
    fun openingBrace(start: P, end: P)

    fun buildArg(): ExpressionBuilder<CS, P>

    @Error
    fun spacesBeforeSeparator(start: P, end: P)

    @HighlightOnly
    fun separator(start: P, end: P)


    @Error
    fun missedClosingBrace(pos: P)

    @Error
    fun unfinishedClosingBrace(start: P, end: P)

    fun closingBrace(@HighlightOnly start: P, @HighlightOnly end: P)
}