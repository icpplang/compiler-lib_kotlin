package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos

/**
 * todo minimal stub for qualname building
 */
interface ExpressionBuilder<CS : CollectedSubstring, P : Pos> {
    fun buildReferenceOperand(): QualnameUsageBuilder<CS, P>

    @HighlightOnly
    fun subExpressionOpeningBrace(start: P, end: P)

    fun buildSubExpression(): ExpressionBuilder<CS, P>

    @HighlightOnly
    fun subExpressionClosingBrace(start: P, end: P)
}