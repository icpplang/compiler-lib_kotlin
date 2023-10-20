package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Error
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos

/**
 * Interface for building template arguments pass list in expressions.
 */
interface TemplateUsageBuilder<CS : CollectedSubstring, P : Pos> {
    /**
     * Reports error when spaces are between name and template.
     * ```
     * name    $<...>
     *     ^^^^
     * ```
     */
    @Error
    fun spacesBeforeOpeningBrace(start: P, end: P)

    /**
     * Reports error when opening brace `$<` written only partially (e.g. just `$`).
     */
    @Error
    fun unfinishedOpeningBrace(start: P, end: P)

    /**
     * Highlights opening brace (`$<`).
     */
    @HighlightOnly
    fun openingBrace(start: P, end: P)

    /**
     * Returns builder for new argument.
     */
    fun buildArg(): ExpressionBuilder<CS, P>

    /**
     * Reports error when spaces found before argument separator.
     */
    @Error
    fun spacesBeforeSeparator(start: P, end: P)

    /**
     * Highlights arguments separator.
     */
    @HighlightOnly
    fun separator(start: P, end: P)


    /**
     * Reports error when missed separator (`,`) between arguments.
     */
    @Error
    fun missedSeparator(pos: P)

    /**
     * Reports error when closing brace `>` written only partially (now unused).
     */
    @Error
    fun unfinishedClosingBrace(start: P, end: P)

    /**
     * Finalizes builder.
     */
    fun closingBrace(@HighlightOnly start: P, @HighlightOnly end: P)
}