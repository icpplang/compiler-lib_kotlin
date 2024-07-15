package ru.landgrafhomyak.icpp.ast.builders

import ru.landgrafhomyak.icpp.parser.environment.BuilderState
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Finalizer
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Error

@Suppress("FunctionName", "ClassName")
sealed interface NumericLiteralBuilders {
    /**
     * Helper interface for states that can continued by numeric literal. Contains all possible starts of numeric literal.
     *
     * ***Scheme for resolving calls:***
     * - `^`
     * - - `^(?=0)` - [zeroDigit][NumericLiteralBuilders._NumericLiteralFactory.zeroDigit]
     * - - `^(?=[1-9])` - [decimalDigit][NumericLiteralBuilders._NumericLiteralFactory.decimalDigit]
     * - - ``^`+``
     * - - - ``^`+(?=0)`` - [digitSeparators_zero][NumericLiteralBuilders._NumericLiteralFactory.digitSeparators_zero]
     * - - - ``^`+(?=[1-9])`` - [digitSeparators_decimal][NumericLiteralBuilders._NumericLiteralFactory.digitSeparators_decimal]
     * - - - ``^`+(?![`0-9])`` - [justDigitSeparators][NumericLiteralBuilders._NumericLiteralFactory.justDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface _NumericLiteralFactory<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        /**
         * Initiates building of numeric literal that started with digit separators
         * (it is an error, separators must be only between digits) and then non-zero decimal digit.
         * Digits separator symbol ``'`'`` used only in numbers.
         *
         * @param separatorsStart Position of first leading digit separator.
         * @param separatorsEnd Position after the last leading digit separator.
         * @return [Next state][NumericLiteralBuilders.DecimalIntegerPartValue] to collect decimal integer value.
         */
        @Error
        @Finalizer
        fun digitSeparators_decimal(separatorsStart: P, separatorsEnd: P): DecimalIntegerPartValue<P, CS, R>

        /**
         * Initiates building of numeric literal that started with digit separators
         * (it is an error, separators must be only between digits) and then zero digit `'0'`.
         * Digits separator symbol ``'`'`` used only in numbers.
         *
         * @param separatorsStart Position of first leading digit separator.
         * @param separatorsEnd Position after the last leading digit separator.
         * @return [Next state][NumericLiteralBuilders.LeadingZero] to determine [radix][NumericLiteralBuilders.Radix] of integer.
         */
        @Error
        @Finalizer
        fun digitSeparators_zero(separatorsStart: P, separatorsEnd: P): LeadingZero<P, CS, R>

        /**
         * Initiates building of numeric literal that started with zero digit `'0'`.
         *
         * @return [Next state][NumericLiteralBuilders.LeadingZero] to determine [radix][NumericLiteralBuilders.Radix] of integer.
         */
        fun zeroDigit(): LeadingZero<P, CS, R>

        /**
         * Initiates building of numeric literal that started with non-zero decimal digit.
         *
         * @return [Next state][NumericLiteralBuilders.DecimalIntegerPartValue] to collect decimal integer value.
         */
        fun decimalDigit(): DecimalIntegerPartValue<P, CS, R>

        /**
         * Digit separators are found, but there are no digits. Technically it is a digit, but actually not.
         *
         * @param start Position of first digit separator.
         * @param end Position after last digit separator.
         *
         * @return [Next state][NumericLiteralBuilders.GarbageAfterNumericLiteral] to skip unexpected symbols after literal.
         */
        @Error
        fun justDigitSeparators(start: P, end: P): GarbageAfterNumericLiteral<P, CS, R>
    }


    enum class Radix {
        BINARY, OCTAL, HEXADECIMAL
    }


    /**
     * Switch state to dispatch to [non-decimal integer][NonDecimalIntegerValue] (`0b`, `0o`, `0x`)
     * or [decimal number][DecimalIntegerPartValue] states if there is no marker.
     *
     * ***Scheme for resolving calls:***
     * - `0`
     * - - `0[box]`
     * - - - `0b(?![0-1])`, `0o(?![0-7])`, `0x(?![0-9a-fA-F])` - [justSingleZero_radix][NumericLiteralBuilders.LeadingZero.justSingleZero_radix]
     * - - - `0b(?=[0-1])`, `0o(?=[0-7])`, `0x(?=[0-9a-fA-F])` - [justSingleZero_radix_value][NumericLiteralBuilders.LeadingZero.justSingleZero_radix_value]
     * - - - ``0b`+(?![0-1])``, ``0o`+(?![0-7])``, ``0x`+(?![0-9a-fA-F])`` - [justSingleZero_radix_digitSeparators][NumericLiteralBuilders.LeadingZero.justSingleZero_radix_digitSeparators]
     * - - - ``0b`+(?=[0-1])``, ``0o`+(?=[0-7])``, ``0x`+(?=[0-9a-fA-F])`` - [justSingleZero_radix_leadingDigitSeparators_value][NumericLiteralBuilders.LeadingZero.justSingleZero_radix_leadingDigitSeparators_value]
     * - - ``0(?![`0-9])`` - [justSingleDecimalZero][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero]
     * - - ``0` ``
     * - - - ``0`+(?![`0-9])`` - [justSingleDecimalZero_trailingDigitSeparators][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero_trailingDigitSeparators]
     * - - - ``0`(?=[0-9])`` - [leadingDecimalZero_singleDigitSeparator][NumericLiteralBuilders.LeadingZero.leadingDecimalZero_singleDigitSeparator]
     * - - - ``0`{2,}(?=[0-9])`` - [leadingDecimalZero_severalDigitSeparators][NumericLiteralBuilders.LeadingZero.leadingDecimalZero_severalDigitSeparators]
     * - - `0{2,}`
     * - - - ``0{2,}(?![`0-9])`` - [severalDecimalZeros][NumericLiteralBuilders.LeadingZero.severalDecimalZeros]
     * - - - ``0{2,}`+(?![`0-9])`` - [severalDecimalZeros_trailingDigitSeparators][NumericLiteralBuilders.LeadingZero.severalDecimalZeros_trailingDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface LeadingZero<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        /**
         * There is zero and ***known*** radix, but nothing related to numeric literal after them.
         *
         * @param zeroStart Position of leading zero.
         * @param zeroEnd Position after leading zero.
         * @param radixStart Position of first symbol of radix.
         * @param radixEnd Position after last symbol of radix.
         * @param radix Parsed radix of integer.
         * @return [Next state][NumericLiteralBuilders.GarbageAfterNumericLiteral] to skip unexpected symbols after literal.
         */
        @Error
        @Finalizer
        fun justSingleZero_radix(zeroStart: P, zeroEnd: P, radixStart: P, radixEnd: P, radix: Radix): GarbageAfterNumericLiteral<P, CS, R>

        /**
         * There is zero, ***known*** radix and first digit of value.
         *
         * @param zeroStart Position of leading zero.
         * @param zeroEnd Position after leading zero.
         * @param radixStart Position of first symbol of radix.
         * @param radixEnd Position after last symbol of radix.
         * @param radix Parsed radix value.
         * @return [Next state][NumericLiteralBuilders.NonDecimalIntegerValue] to collect integer value.
         */
        @Finalizer
        fun justSingleZero_radix_value(zeroStart: P, zeroEnd: P, radixStart: P, radixEnd: P, radix: Radix): NonDecimalIntegerValue<P, CS, R>

        /**
         * There is zero, ***known*** radix and digit separators, but nothing related to numeric literal after them.
         *
         * @param zeroStart Position of leading zero.
         * @param zeroEnd Position after leading zero.
         * @param radixStart Position of first symbol of radix.
         * @param radixEnd Position after last symbol of radix.
         * @param radix Parsed radix value.
         * @param separatorsStart Position of first digit separator.
         * @param separatorsEnd Position after last digit separator.
         * @return [Next state][NumericLiteralBuilders.GarbageAfterNumericLiteral] to skip unexpected symbols after literal.
         */
        @Error
        @Finalizer
        fun justSingleZero_radix_digitSeparators(
            zeroStart: P, zeroEnd: P,
            radixStart: P, radixEnd: P, radix: Radix,
            separatorsStart: P, separatorsEnd: P
        ): GarbageAfterNumericLiteral<P, CS, R>

        /**
         * There is zero, ***known*** radix and digit separators, then first digit of value.
         *
         * @param zeroStart Position of leading zero.
         * @param zeroEnd Position after leading zero.
         * @param radixStart Position of first symbol of radix.
         * @param radixEnd Position after last symbol of radix.
         * @param radix Parsed radix value.
         * @param leadingSeparatorsStart Position of first leading digit separator.
         * @param leadingSeparatorsEnd Position after last leading digit separator.
         * @return [Next state][NumericLiteralBuilders.NonDecimalIntegerValue] to collect integer value.
         */
        @Error
        @Finalizer
        fun justSingleZero_radix_leadingDigitSeparators_value(
            zeroStart: P, zeroEnd: P,
            radixStart: P, radixEnd: P, radix: Radix,
            leadingSeparatorsStart: P, leadingSeparatorsEnd: P
        ): NonDecimalIntegerValue<P, CS, R>

        /**
         * There are no any other digits or separators after zero, but possible dot `'.'` that means start of fractional part.
         *
         * @param zeroStart Position of zero.
         * @param zeroEnd Position after zero.
         * @return [Next state][NumericLiteralBuilders.DecimalFractionalPartDot] to build optional fractional part.
         */
        @Finalizer
        fun justSingleDecimalZero(zeroStart: P, zeroEnd: P): DecimalFractionalPartDot<P, CS, R>

        /**
         * There are no any other digits after zero, but some trailing separators are,
         * also, possible dot `'.'` that means start of fractional part.
         *
         * @param zeroStart Position of zero.
         * @param zeroEnd Position after zero.
         * @param trailingSeparatorsStart position of first trailing digit separator.
         * @param trailingSeparatorsEnd position after last trailing digit separator.
         * @return [Next state][NumericLiteralBuilders.DecimalFractionalPartDot] to build optional fractional part.
         */
        @Error
        @Finalizer
        fun justSingleDecimalZero_trailingDigitSeparators(
            zeroStart: P, zeroEnd: P,
            trailingSeparatorsStart: P, trailingSeparatorsEnd: P
        ): DecimalFractionalPartDot<P, CS, R>

        /**
         * Literal contains only zeros `'0'` (at least two, otherwise
         * [`justSingleDecimalZero`][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero] will be called).
         * If there are digits after zeros or digit separators,
         * [`leadingDecimalZero`][NumericLiteralBuilders.LeadingZero.leadingDecimalZero] or
         * [`severalDecimalZeros_trailingDigitSeparators`][NumericLiteralBuilders.LeadingZero.severalDecimalZeros_trailingDigitSeparators]
         * will be called.
         *
         * @param zerosStart Position of first zero.
         * @param zerosEnd Position after last zero.
         * @return [Next state][NumericLiteralBuilders.DecimalFractionalPartDot] to build optional fractional part.
         */
        @Error
        @Finalizer
        fun severalDecimalZeros(zerosStart: P, zerosEnd: P): DecimalFractionalPartDot<P, CS, R>

        /**
         * Literal contains only zeros `'0'` (at least two, otherwise
         * [`leadingDecimalZero_singleDigitSeparator`][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero]
         * or [`leadingDecimalZero_severalDigitSeparators`][NumericLiteralBuilders.LeadingZero.leadingDecimalZero_severalDigitSeparators]
         * will be called) followed by trailing digit separators (without any digits after them, otherwise
         * [`leadingDecimalZero`][NumericLiteralBuilders.LeadingZero.leadingDecimalZero] will be called).
         *
         * @param zerosStart Position of first zero.
         * @param zerosEnd Position after last zero.
         * @param trailingSeparatorsStart Position of first trailing digit separator.
         * @param trailingSeparatorsEnd Position after last trailing digit separator.
         * @return [Next state][NumericLiteralBuilders.DecimalFractionalPartDot] to build optional fractional part.
         */
        @Error
        @Finalizer
        fun severalDecimalZeros_trailingDigitSeparators(
            zerosStart: P, zerosEnd: P,
            trailingSeparatorsStart: P, trailingSeparatorsEnd: P
        ): DecimalFractionalPartDot<P, CS, R>

        /**
         * Literal starts with zero `'0'` followed by another decimal digit. If zero followed by digit separators
         * [`leadingDecimalZero_singleDigitSeparator`][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero]
         * or [`leadingDecimalZero_severalDigitSeparators`][NumericLiteralBuilders.LeadingZero.leadingDecimalZero_severalDigitSeparators]
         * will be called. If there is nothing after zero, [`justSingleDecimalZero`][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero]
         * will be called. No matter if there is one or many zeros, only first will be marked.
         *
         * @param zeroStart Position of zero.
         * @param zeroEnd Position after zero.
         * @return [Next state][NumericLiteralBuilders.DecimalIntegerPartValue] to collect decimal integer value.
         */
        @Finalizer
        fun leadingDecimalZero(zeroStart: P, zeroEnd: P): DecimalIntegerPartValue<P, CS, R>

        /**
         * Literal starts with zero `'0'` followed by single digit separator ``'`'`` (if many,
         * [`leadingDecimalZero_severalDigitSeparators`][NumericLiteralBuilders.LeadingZero.leadingDecimalZero_severalDigitSeparators]
         * will be called) and decimal digits then (if not,
         * [`justSingleDecimalZero_trailingDigitSeparators`][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero_trailingDigitSeparators]
         * will be called).
         *
         * @param zeroStart Position of zero.
         * @param zeroEnd Position after zero.
         * @return [Next state][NumericLiteralBuilders.DecimalIntegerPartValue] to collect decimal integer value.
         */
        @Error
        @Finalizer
        fun leadingDecimalZero_singleDigitSeparator(zeroStart: P, zeroEnd: P, separatorStart: P, separatorEnd: P): DecimalIntegerPartValue<P, CS, R>

        /**
         * Literal starts with zero `'0'` followed by multiple digit separators ``'`'`` (if single,
         * [`leadingDecimalZero_singleDigitSeparator`][NumericLiteralBuilders.LeadingZero.leadingDecimalZero_singleDigitSeparator]
         * will be called) and decimal digits then (if not,
         * [`justSingleDecimalZero_trailingDigitSeparators`][NumericLiteralBuilders.LeadingZero.justSingleDecimalZero_trailingDigitSeparators]
         * will be called).
         *
         * @param zeroStart Position of zero.
         * @param zeroEnd Position after zero.
         * @return [Next state][NumericLiteralBuilders.DecimalIntegerPartValue] to collect decimal integer value.
         */
        @Error
        @Finalizer
        fun leadingDecimalZero_severalDigitSeparators(zeroStart: P, zeroEnd: P, separatorsStart: P, separatorsEnd: P): DecimalIntegerPartValue<P, CS, R>
    }

    /**
     * State for collecting non-decimal integer value.
     *
     * ***Scheme for resolving calls:***
     *
     * (`\d` matches depend on radix)
     *
     * - `\d+`
     * - - `\d+(?!\d)` - [lastNonDecimalIntegerValue][NumericLiteralBuilders.NonDecimalIntegerValue.lastNonDecimalIntegerValue]
     * - - ``\d+` ``
     * - - - ``\d+`+(?!\d)``- [lastNonDecimalIntegerValue_trailingDigitSeparators][NumericLiteralBuilders.NonDecimalIntegerValue.lastNonDecimalIntegerValue_trailingDigitSeparators]
     * - - - ``\d+`(?=\d)`` - [nonDecimalIntegerValue_singleDigitSeparator][NumericLiteralBuilders.NonDecimalIntegerValue.nonDecimalIntegerValue_singleDigitSeparator]
     * - - - ``\d+`{2,}(?=\d)`` - [nonDecimalIntegerValue_severalDigitSeparators][NumericLiteralBuilders.NonDecimalIntegerValue.nonDecimalIntegerValue_severalDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface NonDecimalIntegerValue<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        /**
         * Last chunk of digits. There are no digits or separators after chunk.
         * If chunk followed by separators, [lastNonDecimalIntegerValue_trailingDigitSeparators][NumericLiteralBuilders.NonDecimalIntegerValue.lastNonDecimalIntegerValue_trailingDigitSeparators]
         * will be called.
         *
         * @param value Collected digits.
         * @return [Next state][NumericLiteralBuilders.GarbageAfterNumericLiteral] to skip unexpected symbols after literal.
         */
        @Finalizer
        fun lastNonDecimalIntegerValue(value: CS): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun lastNonDecimalIntegerValue_trailingDigitSeparators(value: CS, trailingSeparatorStart: P, trailingSeparatorEnd: P): GarbageAfterNumericLiteral<P, CS, R>


        fun nonDecimalIntegerValue_singleDigitSeparator(value: CS, separatorStart: P, separatorEnd: P)

        @Error
        fun nonDecimalIntegerValue_severalDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P)
    }

    /**
     * State for collecting decimal integer value.
     *
     * ***Scheme for resolving calls:***
     * - `[0-9]+`
     * - - `[0-9]+(?![0-9])` - [lastIntegerPartValue][NumericLiteralBuilders.DecimalIntegerPartValue.lastIntegerPartValue]
     * - - ``[0-9]+` ``
     * - - - ``[0-9]+`+(?![0-9])``- [lastIntegerPartValue_trailingDigitSeparators][NumericLiteralBuilders.DecimalIntegerPartValue.lastIntegerPartValue_trailingDigitSeparators]
     * - - - ``[0-9]+`(?=[0-9])`` - [integerPartValue_singleDigitSeparator][NumericLiteralBuilders.DecimalIntegerPartValue.integerPartValue_singleDigitSeparator]
     * - - - ``[0-9]+`{2,}(?=[0-9])`` - [integerPartValue_severalDigitSeparators][NumericLiteralBuilders.DecimalIntegerPartValue.integerPartValue_severalDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface DecimalIntegerPartValue<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        @Finalizer
        fun lastIntegerPartValue(value: CS): DecimalFractionalPartDot<P, CS, R>

        @Error
        @Finalizer
        fun lastIntegerPartValue_trailingDigitSeparators(value: CS, trailingSeparatorStart: P, trailingSeparatorEnd: P): DecimalFractionalPartDot<P, CS, R>


        fun integerPartValue_singleDigitSeparator(value: CS, separatorStart: P, separatorEnd: P)

        @Error
        fun integerPartValue_severalDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P)
    }

    /**
     * State to determine is there fractional part of number or not.
     *
     * ***Scheme for resolving calls:***
     * - `(?!.)` - [noFractionalPart][NumericLiteralBuilders.DecimalFractionalPartDot.noFractionalPart]
     * - `.`
     * - - ``.(?![`0-9])``- [dot][NumericLiteralBuilders.DecimalFractionalPartDot.dot]
     * - - `.(?=[0-9])` - [dot_value][NumericLiteralBuilders.DecimalFractionalPartDot.dot_value]
     * - - ``.`+`` - [dot_leadingDigitSeparators_value][NumericLiteralBuilders.DecimalFractionalPartDot.dot_leadingDigitSeparators_value]
     * - - - ``.`+(?=[0-9])`` - [dot_leadingDigitSeparators_value][NumericLiteralBuilders.DecimalFractionalPartDot.dot_leadingDigitSeparators_value]
     * - - - ``.`+(?![`0-9])`` - [dot_justDigitSeparators][NumericLiteralBuilders.DecimalFractionalPartDot.dot_justDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface DecimalFractionalPartDot<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        @Finalizer
        fun noFractionalPart(): Exponent<P, CS, R>

        @Error
        @Finalizer
        fun dot(dotStart: P, dotEnd: P): Exponent<P, CS, R>


        @Finalizer
        fun dot_value(dotStart: P, dotEnd: P): DecimalFractionalPartValue<P, CS, R>

        @Error
        @Finalizer
        fun dot_leadingDigitSeparators_value(dotStart: P, dotEnd: P, separatorsStart: P, separatorsEnd: P): DecimalFractionalPartValue<P, CS, R>

        @Error
        @Finalizer
        fun dot_justDigitSeparators(dotStart: P, dotEnd: P, separatorsStart: P, separatorsEnd: P): Exponent<P, CS, R>
    }

    /**
     * State for collecting decimal integer value.
     *
     * ***Scheme for resolving calls:***
     * - `[0-9]+`
     * - - `[0-9]+(?![0-9])` - [lastFractionalPartValue][NumericLiteralBuilders.DecimalFractionalPartValue.lastFractionalPartValue]
     * - - ``[0-9]+` ``
     * - - - ``[0-9]+`+(?![0-9])``- [lastFractionalPartValue_trailingDigitSeparators][NumericLiteralBuilders.DecimalFractionalPartValue.lastFractionalPartValue_trailingDigitSeparators]
     * - - - ``[0-9]+`(?=[0-9])`` - [fractionalPartValue_singleDigitSeparator][NumericLiteralBuilders.DecimalFractionalPartValue.fractionalPartValue_singleDigitSeparator]
     * - - - ``[0-9]+`{2,}(?=[0-9])`` - [fractionalPartValue_severalDigitSeparators][NumericLiteralBuilders.DecimalFractionalPartValue.fractionalPartValue_severalDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface DecimalFractionalPartValue<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        @Finalizer
        fun lastFractionalPartValue(value: CS): Exponent<P, CS, R>

        @Error
        @Finalizer
        fun lastFractionalPartValue_trailingDigitSeparators(value: CS, trailingSeparatorStart: P, trailingSeparatorEnd: P): Exponent<P, CS, R>


        fun fractionalPartValue_singleDigitSeparator(value: CS, separatorStart: P, separatorEnd: P)

        @Error
        fun fractionalPartValue_severalDigitSeparators(value: CS, separatorsStart: P, separatorsEnd: P)
    }

    enum class ExponentSign {
        PLUS, MINUS
    }

    /**
     * State for collecting exponent part if it is.
     *
     * ***Scheme for resolving calls:***
     * - `(?!e)` - [noExponent][NumericLiteralBuilders.Exponent.noExponent]
     * - `e`
     * - - ``e(?![`\+\-0-9])`` - [justE][NumericLiteralBuilders.Exponent.justE]
     * - - ``e` ``
     * - - - ``e`+(?![`0-9])`` - [e_digitSeparators][NumericLiteralBuilders.Exponent.e_digitSeparators]
     * - - - ``e`+[0-9]+(?![`0-9])`` - [e_leadingDigitSeparators_value][NumericLiteralBuilders.Exponent.e_leadingDigitSeparators_value]
     * - - `e[0-9]`
     * - - - ``e[0-9]+(?![`0-9])`` - [e_value][NumericLiteralBuilders.Exponent.e_value]
     * - - - ``e[0-9]+`+(?![`0-9])`` - [e_value_trailingDigitSeparators][NumericLiteralBuilders.Exponent.e_value_trailingDigitSeparators]
     * - - `e[\+\-]`
     * - - - `e[\+\-](?![`0-9])` - [e_sign][NumericLiteralBuilders.Exponent.e_sign]
     * - - - ``e[\+\-]` ``
     * - - - - ``e[\+\-]`+(?![`0-9])`` - [e_sign_digitSeparators][NumericLiteralBuilders.Exponent.e_sign_digitSeparators]
     * - - - - ``e[\+\-]`+[0-9]+(?![`0-9])`` - [e_sign_leadingDigitSeparators_value][NumericLiteralBuilders.Exponent.e_sign_leadingDigitSeparators_value]
     * - - - `e[\+\-][0-9]`
     * - - - - ``e[\+\-][0-9]+(?![`0-9])`` - [e_sign_value][NumericLiteralBuilders.Exponent.e_sign_value]
     * - - - - ``e[\+\-][0-9]+`+(?![`0-9])`` - [e_sign_value_trailingDigitSeparators][NumericLiteralBuilders.Exponent.e_sign_value_trailingDigitSeparators]
     *
     * @param P Type of [position][Pos] implementation expected by this builder.
     * @param CS Type of [collected substring][CollectedSubstring] implementation expected by this builder.
     * @param R State that will be returned after finishing parsing numeric literal.
     */
    interface Exponent<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        @Finalizer
        fun noExponent(): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun justE(
            eStart: P, eEnd: P
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_value(
            eStart: P, eEnd: P,
            value: CS
        ): GarbageAfterNumericLiteral<P, CS, R>


        @Error
        @Finalizer
        fun e_digitSeparators(
            eStart: P, eEnd: P,
            separatorsStart: P, separatorsEnd: P
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_leadingDigitSeparators_value(
            eStart: P, eEnd: P,
            separatorsStart: P, separatorsEnd: P,
            value: CS
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_value_trailingDigitSeparators(
            eStart: P, eEnd: P,
            value: CS,
            separatorsStart: P, separatorsEnd: P
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_sign(
            eStart: P, eEnd: P,
            signStart: P, signEnd: P, sign: ExponentSign
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_sign_digitSeparators(
            eStart: P, eEnd: P,
            signStart: P, signEnd: P, sign: ExponentSign,
            separatosStart: P, separatorsEnd: P,
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Finalizer
        fun e_sign_value(
            eStart: P, eEnd: P,
            signStart: P, signEnd: P, sign: ExponentSign,
            value: CS
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_sign_leadingDigitSeparators_value(
            eStart: P, eEnd: P,
            signStart: P, signEnd: P, sign: ExponentSign,
            separatorsStart: P, separatorsEnd: P,
            value: CS
        ): GarbageAfterNumericLiteral<P, CS, R>

        @Error
        @Finalizer
        fun e_sign_value_trailingDigitSeparators(
            eStart: P, eEnd: P,
            signStart: P, signEnd: P, sign: ExponentSign,
            value: CS,
            separatorsStart: P, separatorsEnd: P
        ): GarbageAfterNumericLiteral<P, CS, R>
    }

    interface GarbageAfterNumericLiteral<in P : Pos, in CS : CollectedSubstring, out R> : BuilderState<P, CS, R> {
        @Finalizer
        fun noGarbage(): R

        @Error
        @Finalizer
        fun garbage(start: P, end: P): R
    }
}