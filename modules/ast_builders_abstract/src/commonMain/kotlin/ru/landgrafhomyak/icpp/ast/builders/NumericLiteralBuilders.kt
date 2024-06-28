package ru.landgrafhomyak.icpp.ast.builders

import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Finalizer
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Error

@Suppress("FunctionName", "ClassName")
object NumericLiteralBuilders {
    interface _NumericLiteralFactory<P : Pos, CS : CollectedSubstring, R> {
        @Error
        @Finalizer
        fun digitSeparatorsThenDecimal(separatorsStart: P, separatorsEnd: P): DecimalValue<P, CS, R>

        @Error
        @Finalizer
        fun digitSeparatorsThenZero(separatorsStart: P, separatorsEnd: P): LeadingZero<P, CS, R>

        fun zeroDigit(): LeadingZero<P, CS, R>

        fun decimalDigit(): DecimalValue<P, CS, R>

        @Error
        fun justDigitSeparators(start: P, end: P): GarbageAfterNumericLiteral<P, CS, R>
    }


    enum class IntegerBase {
        BINARY, OCTAL, HEXADECIMAL
    }

    interface LeadingZero<P : Pos, CS : CollectedSubstring, R> {
        @Error
        @Finalizer
        fun justSingleZeroThenBasedIntegerMark(zeroStart: P, zeroEnd: P, baseMarkStart: P, baseMarkEnd: P, base: IntegerBase): GarbageAfterNumericLiteral<P, CS, R>

        @Finalizer
        fun justSingleZeroThenBasedIntegerMarkThenValue(zeroStart: P, zeroEnd: P, baseMarkStart: P, baseMarkEnd: P, base: IntegerBase): BasedIntegerValue<P, CS, R>


        @Error
        @Finalizer
        fun justSingleZeroThenBasedIntegerMarkThenDigitSeparators(
            zeroStart: P, zeroEnd: P,
            baseMarkStart: P, baseMarkEnd: P, base: IntegerBase,
            leadingSeparatorsStart: P, leadingSeparatorsEnd: P
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun justSingleZeroThenBasedIntegerMarkThenLeadingDigitSeparatorsThenValue(
            zeroStart: P, zeroEnd: P,
            baseMarkStart: P, baseMarkEnd: P, base: IntegerBase,
            leadingSeparatorsStart: P, leadingSeparatorsEnd: P
        ): BasedIntegerValue<P, CS, R>

        @Finalizer
        fun justSingleDecimalZero(zeroStart: P, zeroEnd: P): FloatingNumberDot<P, CS, R>

        @Error
        @Finalizer
        fun justSingleDecimalZeroWithTrailingDigitSeparators(zeroStart: P, zeroEnd: P, trailingSeparatorsStart: P, trailingSeparatorsEnd: P): FloatingNumberDot<P, CS, R>


        @Error
        @Finalizer
        fun severalDecimalZeros(zerosStart: P, zerosEnd: P): FloatingNumberDot<P, CS, R>

        @Error
        @Finalizer
        fun severalDecimalZerosWithTrailingDigitSeparators(zeroStart: P, zeroEnd: P, trailingSeparatorsStart: P, trailingSeparatorsEnd: P): FloatingNumberDot<P, CS, R>


        @Finalizer
        fun leadingDecimalZero(zeroStart: P, zeroEnd: P): DecimalValue<P, CS, R>

        @Finalizer
        fun leadingDecimalZeroThenSingleDigitSeparator(zeroStart: P, zeroEnd: P, separatorStart: P, separatorEnd: P): DecimalValue<P, CS, R>

        @Error
        @Finalizer
        fun leadingDecimalZeroThenSeveralDigitSeparator(zeroStart: P, zeroEnd: P, separatorsStart: P, separatorsEnd: P): DecimalValue<P, CS, R>

    }

    interface BasedIntegerValue<P : Pos, CS : CollectedSubstring, R> {
        @Finalizer
        fun lastBasedIntegerValue(value: CS): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun lastBasedIntegerValueThenTrailingDigitSeparators(value: CS, trailingSeparatorStart: P, trailingSeparatorEnd: P): GarbageAfterNumericLiteral<P, CS, R>

        fun basedIntegerValueThenSingleDigitSeparator(value: CS, separatorStart: P, separatorEnd: P)

        @Error
        fun basedIntegerValueThenSeveralDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P)
    }


    interface DecimalValue<P : Pos, CS : CollectedSubstring, R> {
        @Finalizer
        fun lastIntegerPartValue(value: CS): FloatingNumberDot<P, CS, R>

        @Error
        @Finalizer
        fun lastIntegerPartValueThenTrailingDigitSeparators(value: CS, trailingSeparatorStart: P, trailingSeparatorEnd: P): FloatingNumberDot<P, CS, R>


        fun integerPartValueThenSingleDigitSeparator(value: CS, separatorStart: P, separatorEnd: P)

        @Error
        fun integerPartValueThenSeveralDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P)
    }

    interface FloatingNumberDot<P : Pos, CS : CollectedSubstring, R> {
        @Finalizer
        fun noFractionalPart(): ExponentMark<P, CS, R>

        @Finalizer
        fun dot(dotStart: P, dotEnd: P): FractionalPart<P, CS, R>
    }

    interface FractionalPart<P : Pos, CS : CollectedSubstring, R> {
        @Error
        @Finalizer
        fun missedFractionalPart(pos: P): ExponentMark<P, CS, R>

        @Error
        @Finalizer
        fun missedFractionalPartButDigitSeparators(separatorsStart: P, separatorsEnd: P): ExponentMark<P, CS, R>

        @Error
        @Finalizer
        fun leadingDigitSeparatorsBeforeFractionalPartValue(leadingSeparatorsStart: P, leadingSeparatorsEnd: P): FractionalPartEx<P, CS, R>

        @Finalizer
        fun noLeadingDigitSeparatorsBeforeFractionalPartValue(): FractionalPartEx<P, CS, R>
    }

    interface FractionalPartEx<P : Pos, CS : CollectedSubstring, R> {
        @Finalizer
        fun lastFractionalPartValue(value: CS): ExponentMark<P, CS, R>

        @Error
        @Finalizer
        fun lastFractionalPartValueThenTrailingDigitSeparators(value: CS, trailingSeparatorStart: P, trailingSeparatorEnd: P): ExponentMark<P, CS, R>


        fun fractionalPartValueThenSingleDigitSeparator(value: CS, separatorStart: P, separatorEnd: P)

        @Error
        fun fractionalPartValueThenSeveralDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P)
    }

    interface ExponentMark<P : Pos, CS : CollectedSubstring, R> {
        @Finalizer
        fun noExponent(): GarbageAfterNumericLiteral<P, CS, R>

        @Finalizer
        fun exponent(markStart: P, markEnd: P): ExponentSign<P, CS, R>
    }

    interface ExponentSign<P : Pos, CS : CollectedSubstring, R> {
        @Error
        @Finalizer
        fun signMissed(at: P): ExponentValue<P, CS, R>

        @Finalizer
        fun positiveExponent(signStart: P, signEnd: P): ExponentValue<P, CS, R>

        @Finalizer
        fun negativeExponent(signStart: P, signEnd: P): ExponentValue<P, CS, R>
    }


    interface ExponentValue<P : Pos, CS : CollectedSubstring, R> {
        @Error
        @Finalizer
        fun missedExponentValue(at: P): GarbageAfterNumericLiteral<P, CS, R>


        @Error
        @Finalizer
        fun missedExponentValueButDigitsSeparators(start: P): GarbageAfterNumericLiteral<P, CS, R>


        @Finalizer
        fun exponentValue(value: CS): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun exponentValueWithTrailingDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun exponentValueWithLeadingDigitSeparators(separatorsStart: P, separatorsEnd: P, value: CS): GarbageAfterNumericLiteral<P, CS, R>
    }

    interface GarbageAfterNumericLiteral<P : Pos, CS : CollectedSubstring, R> {
        @Finalizer
        fun noGarbage(): R

        @Error
        @Finalizer
        fun garbage(start: P, end: P): R
    }
}