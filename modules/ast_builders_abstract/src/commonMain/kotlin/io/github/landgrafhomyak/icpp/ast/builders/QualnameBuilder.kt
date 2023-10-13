package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.Error


interface QualnameBuilder<CS : CollectedSubstring, P : Pos> {
    fun addLevel(e: CS)

    @Error
    fun unfinishedScopeResolutionOperator(start: P, end: P)

    @HighlightOnly
    fun separator(start: P, end: P)

    @Error
    fun spacesBetweenSeparatorAndName(start: P, end: P)

    @Error
    fun nothingAfterSeparator(p: P)

    @Error
    fun addBadLevel(start: P, end: P)
}
