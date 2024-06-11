package ru.landgrafhomyak.icpp.ast.builders

import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Error
import ru.landgrafhomyak.icpp.parser.environment.Finalizer
import ru.landgrafhomyak.icpp.parser.environment.HighlightOnly
import ru.landgrafhomyak.icpp.parser.environment.Pos

interface NumericLiteralBuilder<P : Pos, CS : CollectedSubstring> {

    interface NumericLiteralFactory<P : Pos, CS : CollectedSubstring> {
        fun numericLiteral(): NumericLiteralBuilder<P, CS>
    }

    fun justZero(@HighlightOnly at: P)

    @Error
    fun justZeros(start: P, end: P)

    @Error
    fun justDigitSeparators(start: P, end: P)

    @Error
    fun justDigitSeparatorsAndZeros(start: P, end: P)

    @HighlightOnly
    fun singleLeadingZero(at: P)

    @Error
    fun multipleLeadingZeros(start: P, end: P)

    enum class Base {
        BINARY, OCTAL, HEX
    }

    fun base(@HighlightOnly start: P, @HighlightOnly end: P, base: Base)

    @Error
    fun missedValueAfterBase(at: P)

    fun integerPart(value: CS)


    @Error
    fun missedFractionalPart(at: P)

    fun floatSeparator(@HighlightOnly at: P)

    fun fractionalPart(value: CS)


    fun digitsSeparator(@HighlightOnly at: P)

    @Error
    fun leadingDigitSeparators(start: P, end: P)

    @Error
    fun leadingDigitSeparatorsAndZeros(start: P, end: P)

    @Error
    fun multipleDigitSeparators(start: P, end: P)

    @Error
    fun trailingDigitSeparators(start: P, end: P)

    fun exponentMark(@HighlightOnly start: P, @HighlightOnly end: P)

    enum class ExponentSign {
        PLUS, MINUS
    }

    fun exponentSign(@HighlightOnly start: P, @HighlightOnly end: P, sign: ExponentSign)

    @Error
    fun missedExponentSign(pos: P)

    @Error
    fun missedExponentValue(pos: P)

    @Finalizer
    fun exponent(s: CS)

    @Finalizer
    fun finish()

    @Error
    fun trailingGarbage(start: P, end: P)

    @Error
    fun dotAfterBasedInteger(at: P)

    @Error
    fun exponentAfterBasedInteger(at: P)
}