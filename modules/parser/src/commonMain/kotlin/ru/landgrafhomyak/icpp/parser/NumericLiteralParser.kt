package ru.landgrafhomyak.icpp.parser

import ru.landgrafhomyak.icpp.ast.builders.NumericLiteralBuilders
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Predicate
import ru.landgrafhomyak.icpp.parser.environment.SourceStream
import ru.landgrafhomyak.icpp.parser.environment.contains

@Suppress(
    "ClassName", "FunctionName", "LocalVariableName", "RemoveRedundantQualifierName", "MemberVisibilityCanBePrivate",
    "LiftReturnOrAssignment", "SimplifyBooleanWithConstants", "UnnecessaryVariable", "MayBeConstant"
)
/**
 * Parses all types of numeric literals:
 * - Integers
 * - - Decimal: `42`, `69`
 * - - Binary: `0b101`
 * - - Octal: `0o777`
 * - - Hexadecimal: `0xFE0F`
 * - Floats
 * - - Simple: `15.2`
 * - - With exponent: `1e+9`, `1.3e-4`
 *
 * @see NumericLiteralParser.tryParseNumericLiteral
 */
object NumericLiteralParser {
    /**
     * Binds [predicate][Predicate] with [integer base][NumericLiteralBuilders.Radix]
     * to avoid typos and mistakes.
     *
     * @see NumericLiteralParser._BasedPredicate.base
     * @see NumericLiteralParser._parseNonDecimalInteger
     */
    sealed class _BasedPredicate(base: NumericLiteralBuilders.Radix) : Predicate {
        /**
         * Integer base which this predicate can parse.
         *
         * @see NumericLiteralParser._BasedPredicate
         */
        @Suppress("CanBePrimaryConstructorProperty")
        val base = base
    }

    /**
     * [Predicate][Predicate] to parse binary values.
     */
    object Digit2Predicate : _BasedPredicate(NumericLiteralBuilders.Radix.BINARY) {

        override fun check(c: Char): Boolean = c == '0' || c == '1'
    }


    /**
     * [Predicate][Predicate] to parse octal values.
     */
    object Digit8Predicate : _BasedPredicate(NumericLiteralBuilders.Radix.OCTAL) {
        override fun check(c: Char): Boolean = c in '0'..'7'
    }

    /**
     * [Predicate][Predicate] to parse decimal values.
     */
    object Digit10Predicate : Predicate {
        override fun check(c: Char): Boolean = c in '0'..'9'
    }


    /**
     * [Predicate][Predicate] to parse hexadecimal values.
     */
    object Digit16Predicate : _BasedPredicate(NumericLiteralBuilders.Radix.HEXADECIMAL) {
        override fun check(c: Char): Boolean = c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'
    }

    /**
     * [Predicate][Predicate] to parse sequences of zeros (`0`).
     */
    object ZeroPredicate : Predicate {
        override fun check(c: Char): Boolean = c == '0'
    }

    /**
     * [Predicate][Predicate] to parse sequences of digit separators (`` ` ``).
     */
    object DigitsSeparatorPredicate : Predicate {
        override fun check(c: Char): Boolean = c == DIGITS_SEPARATOR
    }

    /**
     * Union of [ZeroPredicate][NumericLiteralParser.ZeroPredicate] and
     * [DigitsSeparatorPredicate][NumericLiteralParser.DigitsSeparatorPredicate].
     */
    private object DigitsSeparatorOrZeroPredicate : Predicate {
        override fun check(c: Char): Boolean = c == DIGITS_SEPARATOR || c == '0'
    }

    /**
     * Symbol used to separate digit chunks in big numbers.
     */
    val DIGITS_SEPARATOR = '`'

    /**
     * Symbol used to separate integer part of decimal number from fractional.
     */
    val DOT = '.'

    /**
     * Symbol indicating start of exponent.
     */
    val EXPONENT_MARK_FIRST_CHAR = 'e'

    suspend fun <P : Pos, CS : CollectedSubstring, R> tryParseNumericLiteral(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders._NumericLiteralFactory<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>? {
        if (stream.isEnded)
            return null
        when (stream.current) {
            '0' -> return this.parseZeroDigit(stream, state.zeroDigit())
            in '1'..'9' -> return this.parseDecimalNumericLiteral(stream, state.decimalDigit())
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded)
                    return state.justDigitSeparators(sepsStart, stream.pos)
                when (stream.current) {
                    '0' -> return this.parseZeroDigit(stream, state.digitSeparators_zero(sepsStart, stream.pos))
                    in '1'..'9' -> return this.parseDecimalNumericLiteral(stream, state.digitSeparators_decimal(sepsStart, stream.pos))
                    else -> return state.justDigitSeparators(sepsStart, stream.pos)
                }
            }

            else -> return null
        }
    }


    suspend fun <P : Pos, CS : CollectedSubstring, R> parseZeroDigit(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders.LeadingZero<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        if (stream.isEnded) badParserExpectedCharBadStreamEnded("in '1'..'9'")
        when (stream.current) {
            '0' -> {}
            in '1'..'9' -> badParser("This function must be called after checking that current char is in '1'..'9', current char is '0', so todo") // todo
            else -> badParser("This function must be called after checking that current char is in '1'..'9'")
        }

        val zerosStart = stream.pos
        if (stream.move())
            return this._parseDecimalFractionalPart(stream, state.justSingleDecimalZero(zerosStart, stream.pos))

        when (stream.current) {
            'x' -> return this._parseNonDecimalInteger(stream, state, zerosStart, Digit16Predicate)
            'b' -> return this._parseNonDecimalInteger(stream, state, zerosStart, Digit2Predicate)
            'o' -> return this._parseNonDecimalInteger(stream, state, zerosStart, Digit8Predicate)
            in '1'..'9' -> return this.parseDecimalNumericLiteral(stream, state.leadingDecimalZero(zerosStart, stream.pos))
            '0' -> {
                stream.skip(ZeroPredicate)
                if (stream.isEnded)
                    return this._parseDecimalFractionalPart(stream, state.severalDecimalZeros(zerosStart, stream.pos))
                return this.__dispatchLeadingZero7digitSeparators(
                    stream = stream, zerosStart = zerosStart,
                    justZerosWithTrailingDigitSeparators = state::justSingleDecimalZero_trailingDigitSeparators,
                    leadingZerosThenSingleDigitSeparator = state::leadingDecimalZero_singleDigitSeparator,
                    leadingZerosThenSeveralDigitSeparators = state::leadingDecimalZero_severalDigitSeparators
                )
            }

            DIGITS_SEPARATOR -> {
                return this.__dispatchLeadingZero7digitSeparators(
                    stream = stream, zerosStart = zerosStart,
                    justZerosWithTrailingDigitSeparators = state::severalDecimalZeros_trailingDigitSeparators,
                    leadingZerosThenSingleDigitSeparator = state::leadingDecimalZero_singleDigitSeparator,
                    leadingZerosThenSeveralDigitSeparators = state::leadingDecimalZero_severalDigitSeparators
                )
            }

            else -> return this._parseDecimalFractionalPart(stream, state.justSingleDecimalZero(zerosStart, stream.pos))
        }
    }

    private suspend inline fun <P : Pos, CS : CollectedSubstring, R> __dispatchLeadingZero7digitSeparators(
        stream: SourceStream<P, CS>,
        zerosStart: P,
        justZerosWithTrailingDigitSeparators: (zS: P, zE: P, sS: P, sE: P) -> NumericLiteralBuilders.DecimalFractionalPartDot<P, CS, R>,
        leadingZerosThenSingleDigitSeparator: (zS: P, zE: P, sS: P, sE: P) -> NumericLiteralBuilders.DecimalIntegerPartValue<P, CS, R>,
        leadingZerosThenSeveralDigitSeparators: (zS: P, zE: P, sS: P, sE: P) -> NumericLiteralBuilders.DecimalIntegerPartValue<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        val zerosEnd = stream.pos
        if (stream.move())
            return this._parseDecimalFractionalPart(stream, justZerosWithTrailingDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))

        when (stream.current) {
            DIGITS_SEPARATOR -> {
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded || stream.current !in '0'..'9')
                    return this._parseDecimalFractionalPart(stream, justZerosWithTrailingDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))
                return this._parseDecimalIntegerPart(stream, leadingZerosThenSeveralDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))
            }

            in '0'..'9' -> return this._parseDecimalIntegerPart(stream, leadingZerosThenSingleDigitSeparator(zerosStart, zerosEnd, zerosEnd, stream.pos))

            else -> return this._parseDecimalFractionalPart(stream, justZerosWithTrailingDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))
        }
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseNonDecimalInteger(
        stream: SourceStream<P, CS>,
        zeroState: NumericLiteralBuilders.LeadingZero<P, CS, R>,
        zeroStart: P,
        predicate: _BasedPredicate
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        val zeroEnd = stream.pos
        if (stream.move())
            return zeroState.justSingleZero_radix(zeroStart, zeroEnd, zeroEnd, stream.pos, predicate.base)

        val state: NumericLiteralBuilders.NonDecimalIntegerValue<P, CS, R>
        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded || !predicate.check(stream.current))
                    return zeroState.justSingleZero_radix_digitSeparators(
                        zeroStart, zeroEnd,
                        zeroEnd, sepsStart,
                        predicate.base,
                        sepsStart, stream.pos
                    )
                state = zeroState.justSingleZero_radix_leadingDigitSeparators_value(
                    zeroStart, zeroEnd,
                    zeroEnd, sepsStart,
                    predicate.base,
                    sepsStart, stream.pos
                )
            }

            in predicate -> state = zeroState.justSingleZero_radix_value(
                zeroStart, zeroEnd,
                zeroEnd, stream.pos,
                predicate.base
            )

            else -> return zeroState.justSingleZero_radix(zeroStart, zeroEnd, zeroEnd, stream.pos, predicate.base)
        }

        return this.__collectValue(
            stream = stream,
            predicate = predicate,
            continuation = { _, r -> r },
            lastChunk = state::lastNonDecimalIntegerValue,
            lastChunkThenTrailingDigitSeparators = state::lastNonDecimalIntegerValue_trailingDigitSeparators,
            chunkThenSingleDigitSeparator = state::nonDecimalIntegerValue_singleDigitSeparator,
            chunkThenSeveralDigitSeparators = state::nonDecimalIntegerValue_severalDigitSeparators,
            )
    }


    suspend fun <P : Pos, CS : CollectedSubstring, R> parseDecimalNumericLiteral(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders.DecimalIntegerPartValue<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        if (stream.isEnded) badParserExpectedCharBadStreamEnded("in '1'..'9'")
        when (stream.current) {
            in '1'..'9' -> {}
            '0' -> badParser("This function must be called after checking that current char is in '1'..'9', current char is '0', so todo") // todo
            else -> badParser("This function must be called after checking that current char is in '1'..'9'")
        }


        return this._parseDecimalIntegerPart(stream, state)
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseDecimalIntegerPart(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders.DecimalIntegerPartValue<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        return this.__collectValue(
            stream = stream,
            predicate = Digit10Predicate,
            continuation = this::_parseDecimalFractionalPart,
            lastChunk = state::lastIntegerPartValue,
            lastChunkThenTrailingDigitSeparators = state::lastIntegerPartValue_trailingDigitSeparators,
            chunkThenSingleDigitSeparator = state::integerPartValue_singleDigitSeparator,
            chunkThenSeveralDigitSeparators = state::integerPartValue_severalDigitSeparators
        )
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseDecimalFractionalPart(
        stream: SourceStream<P, CS>,
        dotState: NumericLiteralBuilders.DecimalFractionalPartDot<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        if (stream.isEnded || stream.current != DOT) return this._parseExponent(stream, dotState.noFractionalPart())

        val dotStart = stream.pos
        if (stream.move())
            return dotState.dot(dotStart, stream.pos).noExponent()
        val dotEnd = stream.pos

        val state: NumericLiteralBuilders.DecimalFractionalPartValue<P, CS, R>
        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.current in '0'..'9')
                    state = dotState.dot_leadingDigitSeparators_value(dotStart, dotEnd, sepsStart, stream.pos)
                else
                    return this._parseExponent(stream, dotState.dot_justDigitSeparators(dotStart, dotEnd, sepsStart, stream.pos))
            }

            in '0'..'9' -> state = dotState.dot_value(dotStart, dotEnd)

            else -> return this._parseExponent(stream, dotState.dot(dotStart, dotEnd))
        }

        return this.__collectValue(
            stream = stream,
            predicate = Digit10Predicate,
            continuation = this::_parseExponent,
            lastChunk = state::lastFractionalPartValue,
            lastChunkThenTrailingDigitSeparators = state::lastFractionalPartValue_trailingDigitSeparators,
            chunkThenSingleDigitSeparator = state::fractionalPartValue_singleDigitSeparator,
            chunkThenSeveralDigitSeparators = state::fractionalPartValue_severalDigitSeparators
        )
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseExponent(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders.Exponent<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        if (stream.isEnded || stream.current != EXPONENT_MARK_FIRST_CHAR) return state.noExponent()
        val eStart = stream.pos
        if (stream.move())
            return state.justE(eStart, stream.pos)
        val eEnd = stream.pos

        val sign: NumericLiteralBuilders.ExponentSign
        when (stream.current) {
            '+' -> {
                sign = NumericLiteralBuilders.ExponentSign.PLUS
            }

            '-' -> {
                sign = NumericLiteralBuilders.ExponentSign.MINUS
            }

            else -> {
                return this.__parseExponentValue_noSign(
                    stream,
                    eStart, eEnd,
                    justValue = state::e_value,
                    justSeparators = state::e_digitSeparators,
                    leadingSeparators_value = state::e_leadingDigitSeparators_value,
                    value_trailingSeparators = state::e_value_trailingDigitSeparators,
                    missedValue = state::justE
                )
            }
        }
        val signStart: P = stream.pos
        if (stream.move())
            return state.e_sign(
                eStart, eEnd,
                signStart, stream.pos, sign
            )
        return this.__parseExponentValue_sign(
            stream, state,
            eStart, eEnd,
            signStart, stream.pos, sign
        )
    }


    private inline suspend fun <P : Pos, CS : CollectedSubstring, R> __parseExponentValue_noSign(
        stream: SourceStream<P, CS>,
        eStart: P, eEnd: P,
        justValue: (P, P, CS) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>,
        justSeparators: (P, P, P, P) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>,
        leadingSeparators_value: (P, P, P, P, CS) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>,
        value_trailingSeparators: (P, P, CS, P, P) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>,
        missedValue: (P, P) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded || stream.current !in '0'..'9')
                    return justSeparators(eStart, eEnd, sepsStart, stream.pos)
                val sepsEnd = stream.pos
                return leadingSeparators_value(eStart, eEnd, sepsStart, sepsEnd, stream.collect(Digit10Predicate))
            }

            in '0'..'9' -> {
                val value = stream.collect(Digit10Predicate)
                if (stream.isEnded || stream.current != DIGITS_SEPARATOR)
                    return justValue(eStart, eEnd, value)
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                return value_trailingSeparators(eStart, eEnd, value, sepsStart, stream.pos)
            }

            else -> return missedValue(eStart, eEnd)
        }
    }


    private suspend fun <P : Pos, CS : CollectedSubstring, R> __parseExponentValue_sign(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders.Exponent<P, CS, R>,
        eStart: P, eEnd: P,
        signStart: P, signEnd: P, sign: NumericLiteralBuilders.ExponentSign,
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> =
        this.__parseExponentValue_noSign(
            stream,
            eStart, eEnd,
            justValue = { eS, eE, v ->
                state.e_sign_value(
                    eS, eE,
                    signStart, signEnd, sign, v
                )
            },
            justSeparators = { eS, eE, sS, sE ->
                state.e_sign_digitSeparators(
                    eS, eE,
                    signStart, signEnd, sign,
                    sS, sE
                )
            },
            leadingSeparators_value = { eS, eE, sS, sE, v ->
                state.e_sign_leadingDigitSeparators_value(
                    eS, eE,
                    signStart, signEnd, sign,
                    sS, sE,
                    v
                )
            },
            value_trailingSeparators = { eS, eE, v, sS, sE ->
                state.e_sign_value_trailingDigitSeparators(
                    eS, eE,
                    signStart, signEnd, sign,
                    v,
                    sS, sE
                )
            },
            missedValue = { eS, eE -> state.e_sign(eS, eE, signStart, signEnd, sign) }
        )

    /**
     * Collects digits and separators between them. Stream position must be at digit.
     *
     * @see NumericLiteralBuilders.NonDecimalIntegerValue
     * @see NumericLiteralBuilders.DecimalIntegerPartValue
     * @see NumericLiteralBuilders.DecimalFractionalPartValue
     */
    private suspend inline fun <P : Pos, CS : CollectedSubstring, Z, R> __collectValue(
        stream: SourceStream<P, CS>,
        predicate: Predicate,
        @Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") continuation: suspend (SourceStream<P, CS>, Z) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>,
        lastChunk: (CS) -> Z,
        lastChunkThenTrailingDigitSeparators: (CS, P, P) -> Z,
        chunkThenSingleDigitSeparator: (CS, P, P) -> Unit,
        chunkThenSeveralDigitSeparators: (CS, P, P) -> Unit
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        while (true) {
            val chunk = stream.collect(predicate)
            if (stream.isEnded)
                return continuation(stream, lastChunk(chunk))

            if (stream.current != DIGITS_SEPARATOR)
                return continuation(stream, lastChunk(chunk))

            val sepsStart = stream.pos
            if (stream.move())
                return continuation(stream, lastChunkThenTrailingDigitSeparators(chunk, sepsStart, stream.pos))

            when (stream.current) {
                in predicate -> {
                    chunkThenSingleDigitSeparator(chunk, sepsStart, stream.pos)
                    continue
                }

                DIGITS_SEPARATOR -> {
                    stream.skip(DigitsSeparatorPredicate)
                    if (stream.isEnded)
                        return continuation(stream, lastChunkThenTrailingDigitSeparators(chunk, sepsStart, stream.pos))

                    when (stream.current) {
                        in predicate -> {
                            chunkThenSeveralDigitSeparators(chunk, sepsStart, stream.pos)
                            continue
                        }

                        else -> return continuation(stream, lastChunkThenTrailingDigitSeparators(chunk, sepsStart, stream.pos))
                    }
                }

                else -> return continuation(stream, lastChunkThenTrailingDigitSeparators(chunk, sepsStart, stream.pos))
            }
        }
    }
}