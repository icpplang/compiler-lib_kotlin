package ru.landgrafhomyak.icpp.parser

import ru.landgrafhomyak.icpp.ast.builders.NumericLiteralBuilders
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Predicate
import ru.landgrafhomyak.icpp.parser.environment.SourceStream
import ru.landgrafhomyak.icpp.parser.environment.contains

@Suppress(
    "FunctionName", "RemoveRedundantQualifierName", "MemberVisibilityCanBePrivate",
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
     * Binds [predicate][Predicate] with [integer base][OldNumericLiteralBuilder.Base]
     * to avoid typos and mistakes.
     *
     * @see NumericLiteralParser._BasedPredicate.base
     * @see NumericLiteralParser._parseNonDecimalValue
     */
    sealed class _BasedPredicate(base: NumericLiteralBuilders.IntegerBase) : Predicate {
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
    object Digit2Predicate : _BasedPredicate(NumericLiteralBuilders.IntegerBase.BINARY) {

        override fun check(c: Char): Boolean = c == '0' || c == '1'
    }


    /**
     * [Predicate][Predicate] to parse octal values.
     */
    object Digit8Predicate : _BasedPredicate(NumericLiteralBuilders.IntegerBase.OCTAL) {
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
    object Digit16Predicate : _BasedPredicate(NumericLiteralBuilders.IntegerBase.HEXADECIMAL) {
        override fun check(c: Char): Boolean = c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'
    }

    /**
     * [Predicate][Predicate] to parse sequences of zeros (`0`).
     */
    private object ZeroPredicate : Predicate {
        override fun check(c: Char): Boolean = c == '0'
    }

    /**
     * [Predicate][Predicate] to parse sequences of digit separators (`` ` ``).
     */
    private object DigitsSeparatorPredicate : Predicate {
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

    val DOT = '.'

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
                    '0' -> return this.parseZeroDigit(stream, state.digitSeparatorsThenZero(sepsStart, stream.pos))
                    in '1'..'9' -> return this.parseDecimalNumericLiteral(stream, state.digitSeparatorsThenDecimal(sepsStart, stream.pos))
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
            return this._parseFractionalPart(stream, state.justSingleDecimalZero(zerosStart, stream.pos))

        when (stream.current) {
            'x' -> return this._parseBasedInteger(stream, state, zerosStart, Digit16Predicate)
            'b' -> return this._parseBasedInteger(stream, state, zerosStart, Digit2Predicate)
            'o' -> return this._parseBasedInteger(stream, state, zerosStart, Digit8Predicate)
            in '1'..'9' -> return this.parseDecimalNumericLiteral(stream, state.leadingDecimalZero(zerosStart, stream.pos))
            '0' -> {
                stream.skip(ZeroPredicate)
                if (stream.isEnded)
                    return this._parseFractionalPart(stream, state.severalDecimalZeros(zerosStart, stream.pos))
                return this._dispatchLeadingZero7digitSeparators(
                    stream = stream, zerosStart = zerosStart,
                    justZerosWithTrailingDigitSeparators = state::justSingleDecimalZeroWithTrailingDigitSeparators,
                    leadingZerosThenSingleDigitSeparator = state::leadingDecimalZeroThenSingleDigitSeparator,
                    leadingZerosThenSeveralDigitSeparators = state::leadingDecimalZeroThenSeveralDigitSeparator
                )
            }

            DIGITS_SEPARATOR -> {
                return this._dispatchLeadingZero7digitSeparators(
                    stream = stream, zerosStart = zerosStart,
                    justZerosWithTrailingDigitSeparators = state::severalDecimalZerosWithTrailingDigitSeparators,
                    leadingZerosThenSingleDigitSeparator = state::leadingDecimalZeroThenSingleDigitSeparator,
                    leadingZerosThenSeveralDigitSeparators = state::leadingDecimalZeroThenSeveralDigitSeparator
                )
            }

            else -> return this._parseFractionalPart(stream, state.justSingleDecimalZero(zerosStart, stream.pos))
        }
    }

    private suspend inline fun <P : Pos, CS : CollectedSubstring, R> _dispatchLeadingZero7digitSeparators(
        stream: SourceStream<P, CS>,
        zerosStart: P,
        justZerosWithTrailingDigitSeparators: (zS: P, zE: P, sS: P, sE: P) -> NumericLiteralBuilders.FloatingNumberDot<P, CS, R>,
        leadingZerosThenSingleDigitSeparator: (zS: P, zE: P, sS: P, sE: P) -> NumericLiteralBuilders.DecimalValue<P, CS, R>,
        leadingZerosThenSeveralDigitSeparators: (zS: P, zE: P, sS: P, sE: P) -> NumericLiteralBuilders.DecimalValue<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        val zerosEnd = stream.pos
        if (stream.move())
            return this._parseFractionalPart(stream, justZerosWithTrailingDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))

        when (stream.current) {
            DIGITS_SEPARATOR -> {
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded || stream.current !in '0'..'9')
                    return this._parseFractionalPart(stream, justZerosWithTrailingDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))
                return this._parseDecimalIntegerPart(stream, leadingZerosThenSeveralDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))
            }

            in '0'..'9' -> return this._parseDecimalIntegerPart(stream, leadingZerosThenSingleDigitSeparator(zerosStart, zerosEnd, zerosEnd, stream.pos))

            else -> return this._parseFractionalPart(stream, justZerosWithTrailingDigitSeparators(zerosStart, zerosEnd, zerosEnd, stream.pos))
        }
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseBasedInteger(
        stream: SourceStream<P, CS>,
        zeroState: NumericLiteralBuilders.LeadingZero<P, CS, R>,
        zeroStart: P,
        predicate: _BasedPredicate
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        val zeroEnd = stream.pos
        if (stream.move())
            return zeroState.justSingleZeroThenBasedIntegerMark(zeroStart, zeroEnd, zeroEnd, stream.pos, predicate.base)

        val state: NumericLiteralBuilders.BasedIntegerValue<P, CS, R>
        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded || !predicate.check(stream.current))
                    return zeroState.justSingleZeroThenBasedIntegerMarkThenDigitSeparators(
                        zeroStart, zeroEnd,
                        zeroEnd, sepsStart,
                        predicate.base,
                        sepsStart, stream.pos
                    )
                state = zeroState.justSingleZeroThenBasedIntegerMarkThenLeadingDigitSeparatorsThenValue(
                    zeroStart, zeroEnd,
                    zeroEnd, sepsStart,
                    predicate.base,
                    sepsStart, stream.pos
                )
            }

            in predicate -> state = zeroState.justSingleZeroThenBasedIntegerMarkThenValue(
                zeroStart, zeroEnd,
                zeroEnd, stream.pos,
                predicate.base
            )

            else -> return zeroState.justSingleZeroThenBasedIntegerMark(zeroStart, zeroEnd, zeroEnd, stream.pos, predicate.base)
        }

        return this._collectValue(
            stream = stream,
            continuation = { _, r -> r },
            lastChunk = state::lastBasedIntegerValue,
            lastChunkThenTrailingDigitSeparators = state::lastBasedIntegerValueThenTrailingDigitSeparators,
            chunkThenSingleDigitSeparator = state::basedIntegerValueThenSingleDigitSeparator,
            chunkThenSeveralDigitSeparators = state::basedIntegerValueThenSeveralDigitSeparators,

            )
    }


    suspend fun <P : Pos, CS : CollectedSubstring, R> parseDecimalNumericLiteral(
        stream: SourceStream<P, CS>,
        state: NumericLiteralBuilders.DecimalValue<P, CS, R>
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
        state: NumericLiteralBuilders.DecimalValue<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        return this._collectValue(
            stream = stream,
            continuation = this::_parseFractionalPart,
            lastChunk = state::lastIntegerPartValue,
            lastChunkThenTrailingDigitSeparators = state::lastIntegerPartValueThenTrailingDigitSeparators,
            chunkThenSingleDigitSeparator = state::integerPartValueThenSingleDigitSeparator,
            chunkThenSeveralDigitSeparators = state::integerPartValueThenSeveralDigitSeparators
        )
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseFractionalPart(
        stream: SourceStream<P, CS>,
        dotState: NumericLiteralBuilders.FloatingNumberDot<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        if (stream.isEnded || stream.current != DOT) return this._parseExponent(stream, dotState.noFractionalPart())

        val dotStart = stream.pos
        if (stream.move())
            return dotState.dot(dotStart, stream.pos).missedFractionalPart(stream.pos).noExponent()
        val state = dotState.dot(dotStart, stream.pos)

        val stateEx: NumericLiteralBuilders.FractionalPartEx<P, CS, R>
        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.current in '0'..'9')
                    stateEx = state.leadingDigitSeparatorsBeforeFractionalPartValue(sepsStart, stream.pos)
                else
                    return this._parseExponent(stream, state.missedFractionalPartButDigitSeparators(sepsStart, stream.pos))
            }

            in '0'..'9' -> stateEx = state.noLeadingDigitSeparatorsBeforeFractionalPartValue()

            else -> return this._parseExponent(stream, state.missedFractionalPart(stream.pos))
        }

        return this._collectValue(
            stream = stream,
            continuation = this::_parseExponent,
            lastChunk = stateEx::lastFractionalPartValue,
            lastChunkThenTrailingDigitSeparators = stateEx::lastFractionalPartValueThenTrailingDigitSeparators,
            chunkThenSingleDigitSeparator = stateEx::fractionalPartValueThenSingleDigitSeparator,
            chunkThenSeveralDigitSeparators = stateEx::fractionalPartValueThenSeveralDigitSeparators
        )
    }

    private suspend fun <P : Pos, CS : CollectedSubstring, R> _parseExponent(
        stream: SourceStream<P, CS>,
        markState: NumericLiteralBuilders.ExponentMark<P, CS, R>
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        if (stream.isEnded || stream.current != EXPONENT_MARK_FIRST_CHAR) return markState.noExponent()
        val markStart = stream.pos
        if (stream.move())
            return markState.exponent(markStart, stream.pos).signMissed(stream.pos).missedExponentValue(stream.pos)
        val signState = markState.exponent(markStart, stream.pos)

        val state: NumericLiteralBuilders.ExponentValue<P, CS, R>
        when (stream.current) {
            '+' -> {
                val signStart = stream.pos
                if (stream.move())
                    return signState.positiveExponent(signStart, stream.pos).missedExponentValue(stream.pos)
                state = signState.positiveExponent(signStart, stream.pos)
            }

            '-' -> {
                val signStart = stream.pos
                if (stream.move())
                    return signState.negativeExponent(signStart, stream.pos).missedExponentValue(stream.pos)
                state = signState.negativeExponent(signStart, stream.pos)
            }

            else -> {
                state = signState.signMissed(stream.pos)
            }
        }

        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded || stream.current !in '0'..'9')
                    return state.missedExponentValueButDigitsSeparators(sepsStart)
                val sepsEnd = stream.pos
                return state.exponentValueWithLeadingDigitSeparators(sepsStart, sepsEnd, stream.collect(Digit10Predicate))
            }

            in '0'..'9' -> {
                val value = stream.collect(Digit10Predicate)
                if (stream.isEnded || stream.current != DIGITS_SEPARATOR)
                    return state.exponentValue(value)
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                return state.exponentValueWithTrailingDigitSeparators(value, sepsStart, stream.pos)
            }

            else -> return state.missedExponentValue(stream.pos)
        }
    }


    private suspend inline fun <P : Pos, CS : CollectedSubstring, Z, R> _collectValue(
        stream: SourceStream<P, CS>,
        @Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") continuation: suspend (SourceStream<P, CS>, Z) -> NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R>,
        lastChunk: (CS) -> Z,
        lastChunkThenTrailingDigitSeparators: (CS, P, P) -> Z,
        chunkThenSingleDigitSeparator: (CS, P, P) -> Unit,
        chunkThenSeveralDigitSeparators: (CS, P, P) -> Unit
    ): NumericLiteralBuilders.GarbageAfterNumericLiteral<P, CS, R> {
        while (true) {
            val chunk = stream.collect(Digit10Predicate)
            if (stream.isEnded)
                return continuation(stream, lastChunk(chunk))

            if (stream.current != DIGITS_SEPARATOR)
                return continuation(stream, lastChunk(chunk))

            val sepsStart = stream.pos
            if (stream.move())
                return continuation(stream, lastChunkThenTrailingDigitSeparators(chunk, sepsStart, stream.pos))

            when (stream.current) {
                in '0'..'9' -> {
                    chunkThenSingleDigitSeparator(chunk, sepsStart, stream.pos)
                    continue
                }

                DIGITS_SEPARATOR -> {
                    stream.skip(DigitsSeparatorPredicate)
                    if (stream.isEnded)
                        return continuation(stream, lastChunkThenTrailingDigitSeparators(chunk, sepsStart, stream.pos))

                    when (stream.current) {
                        in '0'..'9' -> {
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