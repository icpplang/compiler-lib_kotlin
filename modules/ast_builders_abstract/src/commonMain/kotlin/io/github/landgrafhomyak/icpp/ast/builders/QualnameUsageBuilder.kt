package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.Error

/**
 * Interface for building qualnames in expressions.
 */
interface QualnameUsageBuilder<CS : CollectedSubstring, P : Pos> {
    /**
     * Adds new level without template to qualname.
     * ```
     * name1::name2$<template1>.name3
     * ^^^^^                    ^^^^^
     * ```
     */
    fun addLevel(e: CS)

    /**
     * Adds new level with template to qualname.
     * ```
     * name1::name2$<template1>.name3
     *        ^^^^^
     * ```
     */
    fun addTemplatedLevel(e: CS): TemplateUsageBuilder<CS, P>

    /**
     * Reports syntax error when scope resolution operator `::` written only partially (e.g. just `:`).
     */
    @Error
    fun unfinishedScopeResolutionOperator(start: P, end: P)

    /**
     * Reports syntax error when member access operator `.` written only partially (now unused).
     */
    @Error
    fun unfinishedMemberAccessOperator(start: P, end: P)

    /**
     * Adds scope resolution operator (`::`).
     */
    fun scopeResolutionOperator(@HighlightOnly start: P, @HighlightOnly end: P)

    /**
     * Reports error when scope resolution operator used after member access operator.
     * ```
     * name1::name2.name3::name4
     *                   ^^
     * ```
     */
    @Error
    fun scopeOfMemberResolutionOperator(start: P, end: P)

    /**
     * Adds member access operator (`.`).
     */
    fun memberAccessOperator(@HighlightOnly start: P, @HighlightOnly end: P)

    /**
     * Reports spaces between operators and names (now it is an error).
     * ```
     * name1  ::  name2 .   name3
     *     ^^^  ^^     ^ ^^^
     * ```
     */
    @Error
    fun spacesBetweenSeparatorAndName(start: P, end: P)

    /**
     * Reports error when missed level after operator.
     * ```
     * name1::name2::
     *               ^
     * ```
     */
    @Error
    fun nothingAfterSeparator(p: P)

    /**
     * Reports ill-formatted level without template (e.g. started with digit).
     * @see QualnameUsageBuilder.addLevel
     */
    @Error
    fun addBadLevel(start: P, end: P)

    /**
     * Reports ill-formatted level with template (e.g. started with digit).
     * @see QualnameUsageBuilder.addTemplatedLevel
     */
    @Error
    fun addBadTemplatedLevel(start: P, end: P): TemplateUsageBuilder<CS, P>
}
