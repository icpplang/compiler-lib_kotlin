package ru.landgrafhomyak.icpp.parser

import ru.landgrafhomyak.icpp.ast.builders.NumericLiteralBuilder
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Predicate
import ru.landgrafhomyak.icpp.parser.environment.SourceStream
import ru.landgrafhomyak.icpp.parser.predicates.OperatorSymbolPredicate
import ru.landgrafhomyak.icpp.parser.predicates.SpaceSymbolsMultilinePredicate

@Suppress("LiftReturnOrAssignment", "FunctionName", "RemoveRedundantQualifierName", "SimplifyBooleanWithConstants")
object NumericLiteralParser {
    private interface BasedPredicate : Predicate {
        val base: NumericLiteralBuilder.Base
    }

    object Digit2Predicate : BasedPredicate {
        override val base: NumericLiteralBuilder.Base
            get() = NumericLiteralBuilder.Base.BINARY

        override fun check(c: Char): Boolean = c == '0' || c == '1'
    }

    object Digit8Predicate : BasedPredicate {
        override val base: NumericLiteralBuilder.Base
            get() = NumericLiteralBuilder.Base.OCTAL

        override fun check(c: Char): Boolean = c in '0'..'7'
    }

    object Digit10Predicate : Predicate {
        override fun check(c: Char): Boolean = c in '0'..'9'
    }

    object Digit16Predicate : BasedPredicate {
        override val base: NumericLiteralBuilder.Base
            get() = NumericLiteralBuilder.Base.HEX

        override fun check(c: Char): Boolean = c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'
    }

    private object ZeroPredicate : Predicate {
        override fun check(c: Char): Boolean = c == '0'
    }

    private object DigitsSeparatorPredicate : Predicate {
        override fun check(c: Char): Boolean = c == DIGITS_SEPARATOR
    }

    private object DigitsSeparatorOrZeroPredicate : Predicate {
        override fun check(c: Char): Boolean = c == DIGITS_SEPARATOR || c == '0'
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val DIGITS_SEPARATOR = '`'
    suspend fun <P : Pos, CS : CollectedSubstring> parseNumericLiteral(
        stream: SourceStream<P, CS>,
        factory: NumericLiteralBuilder.NumericLiteralFactory<P, CS>
    ): Boolean {
        if (stream.isEnded)
            return false

        val leadingSeparatorsStart: P?
        if (stream.current == DIGITS_SEPARATOR) {
            leadingSeparatorsStart = stream.pos
            stream.skip(DigitsSeparatorPredicate)
        } else {
            leadingSeparatorsStart = null
        }


        val builder: NumericLiteralBuilder<P, CS>
        if (stream.current == '0') {
            builder = factory.numericLiteral()
            val zeroPos = stream.pos
            if (stream.move()) {
                if (leadingSeparatorsStart != null)
                    builder.leadingDigitSeparators(leadingSeparatorsStart, zeroPos)
                builder.justZero(zeroPos)
                builder.finish()
                return true
            }

            val leadingZerosAndSeparators: Boolean
            if (leadingSeparatorsStart == null && stream.current == '0') {
                stream.skip(ZeroPredicate)
                if (stream.isEnded) {
                    builder.justZeros(zeroPos, stream.pos)
                    builder.finish()
                    return false
                }
                if (stream.current != DIGITS_SEPARATOR) {
                    builder.multipleLeadingZeros(zeroPos, stream.pos)
                }
            }
            if (stream.current == DIGITS_SEPARATOR || stream.current == '0') {
                stream.skip(DigitsSeparatorOrZeroPredicate)
                if (stream.isEnded) {
                    builder.justDigitSeparatorsAndZeros(leadingSeparatorsStart ?: zeroPos, stream.pos)
                    builder.finish()
                    return true
                }
                leadingZerosAndSeparators = true
            } else {
                leadingZerosAndSeparators = false
            }

            when (stream.current) {
                in '1'..'9' -> {
                    if (leadingSeparatorsStart != null || leadingZerosAndSeparators)
                        builder.leadingDigitSeparatorsAndZeros(leadingSeparatorsStart ?: zeroPos, stream.pos)

                    NumericLiteralParser._parseIntegerPart(stream, builder, Digit10Predicate)
                    return true
                }

                'b' -> {
                    NumericLiteralParser._dispatchLeadingZerosAndSeparators(
                        stream, builder,
                        leadingSeparatorsStart, leadingZerosAndSeparators, zeroPos
                    )
                    val baseStart = stream.pos
                    stream.move()
                    NumericLiteralParser._parseNonDecimalValue(
                        stream, builder, Digit2Predicate,
                        baseStart
                    )
                    return true
                }

                'o' -> {
                    NumericLiteralParser._dispatchLeadingZerosAndSeparators(
                        stream, builder,
                        leadingSeparatorsStart, leadingZerosAndSeparators, zeroPos
                    )
                    val baseStart = stream.pos
                    stream.move()
                    NumericLiteralParser._parseNonDecimalValue(
                        stream, builder, Digit8Predicate,
                        baseStart
                    )
                    return true
                }

                'x' -> {
                    NumericLiteralParser._dispatchLeadingZerosAndSeparators(
                        stream, builder,
                        leadingSeparatorsStart, leadingZerosAndSeparators, zeroPos
                    )

                    val baseStart = stream.pos
                    stream.move()

                    NumericLiteralParser._parseNonDecimalValue(
                        stream, builder, Digit16Predicate,
                        baseStart
                    )
                    return true
                }

                'e' -> {
                    NumericLiteralParser._dispatchLeadingZerosAndSeparators(
                        stream, builder,
                        leadingSeparatorsStart, leadingZerosAndSeparators, zeroPos
                    )
                    NumericLiteralParser._parseExponent(stream, builder)
                    return true
                }

                '.' -> {
                    NumericLiteralParser._dispatchLeadingZerosAndSeparators(
                        stream, builder,
                        leadingSeparatorsStart, leadingZerosAndSeparators, zeroPos
                    )
                    NumericLiteralParser._parseExponent(stream, builder)
                    return true
                }

                else -> {
                    builder.justDigitSeparatorsAndZeros(leadingSeparatorsStart ?: zeroPos, stream.pos)
                    NumericLiteralParser._skipTrailingGarbage(stream, builder)
                    return true
                }
            }
        } else if (stream.current in '1'..'9') {
            builder = factory.numericLiteral()
            if (leadingSeparatorsStart != null)
                builder.leadingDigitSeparators(leadingSeparatorsStart, stream.pos)
            NumericLiteralParser._parseIntegerPart(stream, builder, Digit10Predicate)
            return true
        } else {
            if (leadingSeparatorsStart != null) {
                builder = factory.numericLiteral()
                builder.justDigitSeparators(leadingSeparatorsStart, stream.pos)
                return true
            } else {
                return false
            }
        }
    }

    private fun <P : Pos> _dispatchLeadingZerosAndSeparators(
        stream: SourceStream<P, *>,
        builder: NumericLiteralBuilder<P, *>,
        leadingSeparatorsStart: P?,
        leadingZerosAndSeparators: Boolean,
        zeroPos: P,
    ) {
        @Suppress("KotlinConstantConditions")
        when {
            leadingSeparatorsStart != null && leadingZerosAndSeparators == false -> {
                builder.leadingDigitSeparators(leadingSeparatorsStart, zeroPos)
                builder.singleLeadingZero(zeroPos)
            }

            leadingSeparatorsStart == null && leadingZerosAndSeparators == true -> {
                builder.multipleLeadingZeros(zeroPos, stream.pos)
            }

            leadingSeparatorsStart != null && leadingZerosAndSeparators == true -> {
                builder.leadingDigitSeparatorsAndZeros(leadingSeparatorsStart, stream.pos)
            }
        }
    }

    private suspend fun <P : Pos, CS : CollectedSubstring> _parseNonDecimalValue(
        stream: SourceStream<P, CS>,
        builder: NumericLiteralBuilder<P, CS>,
        predicate: BasedPredicate,

        baseStart: P
    ) {
        builder.base(baseStart, stream.pos, predicate.base)

        if (stream.isEnded) {
            builder.missedValueAfterBase(stream.pos)
            builder.finish()
            return
        }

        NumericLiteralParser._parseIntegerPart(stream, builder, predicate)
    }


    private suspend inline fun <P : Pos, CS : CollectedSubstring> _collectValue(
        stream: SourceStream<P, CS>,
        builder: NumericLiteralBuilder<P, CS>,
        predicate: Predicate,
        receiver: (CS) -> Unit
    ): Any? {
        while (true) {
            receiver(stream.collect(predicate))
            if (stream.isEnded) {
                builder.finish()
                return null
            }
            if (stream.current == DIGITS_SEPARATOR) {
                val separatorsStart = stream.pos
                if (stream.move()) {
                    builder.trailingDigitSeparators(separatorsStart, stream.pos)
                    builder.finish()
                    return null
                }
                if (!predicate.check(stream.current)) {
                    builder.trailingDigitSeparators(separatorsStart, stream.pos)
                    return Unit
                }
                if (stream.current == DIGITS_SEPARATOR) {
                    stream.skip(DigitsSeparatorPredicate)
                    if (stream.isEnded) {
                        builder.trailingDigitSeparators(separatorsStart, stream.pos)
                        builder.finish()
                        return null
                    }
                    if (!predicate.check(stream.current)) {
                        builder.trailingDigitSeparators(separatorsStart, stream.pos)
                        return Unit
                    }
                    builder.multipleDigitSeparators(separatorsStart, stream.pos)
                    continue
                } else {
                    builder.digitsSeparator(separatorsStart)
                }
            }
            if (!predicate.check(stream.current)) {
                return Unit
            }
        }
    }

    private suspend fun <P : Pos, CS : CollectedSubstring> _parseIntegerPart(
        stream: SourceStream<P, CS>,
        builder: NumericLiteralBuilder<P, CS>,
        predicate: Predicate
    ) {
        NumericLiteralParser._collectValue(stream, builder, predicate, builder::integerPart) ?: return

        when (stream.current) {
            'e' -> {
                if (predicate === Digit10Predicate) {
                    NumericLiteralParser._parseExponent(stream, builder)
                    return
                } else {
                    builder.exponentAfterBasedInteger(stream.pos)
                    if (stream.isEnded) {
                        builder.finish()
                        return
                    }
                    NumericLiteralParser._skipTrailingGarbage(stream, builder)
                    return
                }
            }

            '.' -> {
                if (predicate === Digit10Predicate) {
                    NumericLiteralParser._parseFractionalPart(stream, builder)
                    return
                } else {
                    builder.dotAfterBasedInteger(stream.pos)
                    if (stream.isEnded) {
                        builder.finish()
                        return
                    }
                    NumericLiteralParser._skipTrailingGarbage(stream, builder)
                    return
                }
            }

            else -> {
                NumericLiteralParser._skipTrailingGarbage(stream, builder)
                return
            }
        }
    }


    private suspend fun <P : Pos, CS : CollectedSubstring> _parseFractionalPart(
        stream: SourceStream<P, CS>,
        builder: NumericLiteralBuilder<P, CS>
    ) {
        builder.floatSeparator(stream.pos)
        if (stream.move()) {
            builder.missedFractionalPart(stream.pos)
            builder.finish()
            return
        }


        when (stream.current) {
            DIGITS_SEPARATOR -> {
                val sepsStart = stream.pos
                stream.skip(DigitsSeparatorPredicate)
                if (stream.isEnded) {
                    builder.justDigitSeparators(sepsStart, stream.pos)
                    builder.finish()
                    return
                }
                if (stream.current !in '0'..'9') {
                    builder.justDigitSeparators(sepsStart, stream.pos)
                    if (stream.current == 'e') {
                        NumericLiteralParser._parseExponent(stream, builder)
                        return
                    } else {
                        NumericLiteralParser._skipTrailingGarbage(stream, builder)
                        return
                    }
                } else {
                    builder.leadingDigitSeparators(sepsStart, stream.pos)
                    return
                }

            }

            'e' -> {
                builder.missedFractionalPart(stream.pos)
                NumericLiteralParser._parseExponent(stream, builder)
                return
            }

            in '0'..'9' -> {}

            else -> {
                builder.missedFractionalPart(stream.pos)
                NumericLiteralParser._skipTrailingGarbage(stream, builder)
                return
            }
        }

        NumericLiteralParser._collectValue(stream, builder, Digit10Predicate, builder::fractionalPart) ?: return

        if (stream.isEnded) {
            builder.finish()
            return
        }

        if (stream.current == 'e') {
            NumericLiteralParser._parseExponent(stream, builder)
            return
        } else {
            NumericLiteralParser._skipTrailingGarbage(stream, builder)
            return
        }
    }


    private suspend fun <P : Pos, CS : CollectedSubstring> _parseExponent(
        stream: SourceStream<P, CS>,
        builder: NumericLiteralBuilder<P, CS>
    ) {
        val expStart = stream.pos
        if (stream.move()) {
            builder.exponentMark(expStart, stream.pos)
            builder.missedExponentValue(stream.pos)
            builder.finish()
            return
        }

        val sign: NumericLiteralBuilder.ExponentSign?
        when (stream.current) {
            '+' -> {
                sign = NumericLiteralBuilder.ExponentSign.PLUS
            }

            '-' -> {
                sign = NumericLiteralBuilder.ExponentSign.MINUS
            }

            in '0'..'9' -> {
                sign = null
            }

            else -> {
                NumericLiteralParser._skipTrailingGarbage(stream, builder)
                return
            }
        }

        if (sign != null) {
            val signPos = stream.pos
            if (stream.move()) {
                builder.exponentSign(signPos, stream.pos, sign)
                builder.missedExponentValue(stream.pos)
                builder.finish()
                return
            }
            builder.exponentSign(signPos, stream.pos, sign)
            if (stream.current !in '0'..'9') {
                builder.missedExponentValue(stream.pos)
                NumericLiteralParser._skipTrailingGarbage(stream, builder)
                return
            }
        } else {
            if (stream.current !in '0'..'9') {
                builder.missedExponentValue(stream.pos)
                NumericLiteralParser._skipTrailingGarbage(stream, builder)
                return
            }
            builder.missedExponentSign(stream.pos)
        }

        NumericLiteralParser._collectValue(stream, builder, Digit10Predicate, builder::exponent) ?: return
        NumericLiteralParser._skipTrailingGarbage(stream, builder)
    }

    private object DigitLiteralGarbagePredicate : Predicate {
        // todo looks bad
        override fun check(c: Char): Boolean {
            if (SpaceSymbolsMultilinePredicate.check(c)) return false
            if (c == '(' || c == ')') return false
            if (c == '[' || c == ']') return false
            if (c == '{' || c == '}') return false
            if (c == '<' || c == '>') return false
            if (OperatorSymbolPredicate.check(c)) return false
            if (c == '.' || c == ',') return false
            if (c == ':' || c == ';') return false
            if (c == '\'' || c == '\"') return false
            return true
        }
    }

    private suspend fun <P : Pos, CS : CollectedSubstring> _skipTrailingGarbage(
        stream: SourceStream<P, CS>,
        builder: NumericLiteralBuilder<P, CS>
    ) {
        if (stream.isEnded) {
            builder.finish()
            return
        }

        if (SpaceSymbolsMultilinePredicate.check(stream.current)) {
            val garbageStart = stream.pos
            stream.skip(DigitLiteralGarbagePredicate)
            builder.trailingGarbage(garbageStart, stream.pos)
        }

        builder.finish()
    }
}