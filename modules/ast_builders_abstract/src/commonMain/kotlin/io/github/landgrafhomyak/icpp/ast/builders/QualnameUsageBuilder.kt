package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.Error


interface QualnameUsageBuilder<CS : CollectedSubstring, P : Pos> {
    fun addLevel(e: CS)

    fun addTemplatedLevel(e: CS): TemplateUsageBuilder<CS, P>

    @Error
    fun unfinishedScopeResolutionOperator(start: P, end: P)

    @Error
    fun unfinishedMemberAccessOperator(start: P, end: P)

    fun scopeResolutionOperator(@HighlightOnly start: P, @HighlightOnly end: P)

    @Error
    fun scopeOfMemberResolutionOperator(start: P, end: P)

    fun memberAccessOperator(@HighlightOnly start: P, @HighlightOnly end: P)

    @Error
    fun spacesBetweenSeparatorAndName(start: P, end: P)

    @Error
    fun nothingAfterSeparator(p: P)

    @Error
    fun addBadLevel(start: P, end: P)

    @Error
    fun addBadTemplatedLevel(start: P, end: P): TemplateUsageBuilder<CS, P>
}
