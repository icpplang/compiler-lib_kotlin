package ru.landgrafhomyak.icpp.parser

import ru.landgrafhomyak.icpp.ast.builders.StringLiteralBuilder
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Predicate
import ru.landgrafhomyak.icpp.parser.environment.SourceStream

@Suppress("FunctionName", "RemoveRedundantQualifierName", "LiftReturnOrAssignment")
object StringLiteralParser {

    private suspend fun <P : Pos, CS : CollectedSubstring> _parseEscapeSpecialChar(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder<P, CS>,
        escapeStart: P,
        value: StringLiteralBuilder.SpecialChar
    ): Boolean {
        val valueStart = stream.pos
        if (stream.move()) {
            builder.escapeSequenceSpecialChar(escapeStart, valueStart, stream.pos, value)
            builder.unclosedEof(stream.pos)
            builder.finalize()
            return true
        }
        builder.escapeSequenceSpecialChar(escapeStart, valueStart, stream.pos, value)
        return false
    }

    private suspend inline fun <P : Pos, CS : CollectedSubstring> _parseEscapeCode(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder<P, CS>,
        escapeStart: P,
        digitsCount: UInt,
        receiver: (P, P, P, ULong) -> Unit
    ): Boolean {
        if (stream.move()) {
            val end = stream.pos
            builder.unfinishedEscapeSequenceCharCode(escapeStart, end, end)
            builder.unclosedEof(end)
            builder.finalize()
            return true
        }

        var value = 0uL
        var streamIsEnded = false
        val valueStart = stream.pos
        for (i in 0u until digitsCount) {
            if (streamIsEnded) {
                builder.unfinishedEscapeSequenceCharCode(escapeStart, valueStart, stream.pos)
                builder.unclosedEof(stream.pos)
                builder.finalize()
                return true
            }
            when (val c = stream.current) {
                in '0'..'9' -> value = (value shl 4) or ((c.code - '0'.code).toULong())
                in 'a'..'f' -> value = (value shl 4) or ((c.code - 'a'.code + 10).toULong())
                in 'A'..'F' -> value = (value shl 4) or ((c.code - 'A'.code + 10).toULong())
            }
            streamIsEnded = stream.move()
        }

        receiver(escapeStart, valueStart, stream.pos, value)
        return false
    }

    private suspend fun <P : Pos, CS : CollectedSubstring> _parseEscape(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder<P, CS>,
        isMultiline: Boolean
    ): Boolean {
        val escapeStart = stream.pos
        if (stream.move()) {
            val end = stream.pos
            builder.unfinishedEscapeSequenceSpecialChar(escapeStart, end, end)
            builder.unclosedEof(end)
            builder.finalize()
            return true
        }

        when (val c = stream.current) {
            '\\' -> return StringLiteralParser._parseEscapeSpecialChar(
                stream, builder, escapeStart,
                StringLiteralBuilder.SpecialChar.BACK_SLASH
            )


            '0' -> return StringLiteralParser._parseEscapeSpecialChar(
                stream, builder, escapeStart,
                StringLiteralBuilder.SpecialChar.NULL
            )

            'n' -> return StringLiteralParser._parseEscapeSpecialChar(
                stream, builder, escapeStart,
                StringLiteralBuilder.SpecialChar.NEW_LINE
            )

            't' -> return StringLiteralParser._parseEscapeSpecialChar(
                stream, builder, escapeStart,
                StringLiteralBuilder.SpecialChar.TAB
            )

            '\"' -> return StringLiteralParser._parseEscapeSpecialChar(
                stream, builder, escapeStart,
                StringLiteralBuilder.SpecialChar.DOUBLE_QUOTE
            )

            '\'' -> return StringLiteralParser._parseEscapeSpecialChar(
                stream, builder, escapeStart,
                StringLiteralBuilder.SpecialChar.SINGLE_QUOTE
            )

            'x' -> return StringLiteralParser._parseEscapeCode(
                stream, builder, escapeStart, 2u,
            ) { p1, p2, p3, v -> builder.escapeSequenceC8(p1, p2, p3, v.toUByte()) }

            'u' -> return StringLiteralParser._parseEscapeCode(
                stream, builder, escapeStart, 4u,
            ) { p1, p2, p3, v -> builder.escapeSequenceC16(p1, p2, p3, v.toUShort()) }


            'U' -> return StringLiteralParser._parseEscapeCode(
                stream, builder, escapeStart, 8u,
            ) { p1, p2, p3, v -> builder.escapeSequenceC32(p1, p2, p3, v.toUInt()) }

            '\n' -> if (!isMultiline) {
                builder.unclosedEol(stream.pos)
                builder.finalize()
                return true
            } else {
                return false
            }

            else -> {
                val valueStart = stream.pos
                if (stream.isEnded) {
                    builder.unknownEscapeSequenceSpecialChar(escapeStart, valueStart, stream.pos, c)
                    builder.unclosedEof(stream.pos)
                    return true
                }
                builder.unknownEscapeSequenceSpecialChar(escapeStart, valueStart, stream.pos, c)
                stream.move()
                return false
            }
        }
    }


    suspend fun <P : Pos, CS : CollectedSubstring> continueParsingCharLiteral(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder.CharLiteralBuilder<P, CS>
    ) {
        if (stream.isEnded) {
            builder.unclosedEof(stream.pos)
            builder.finalize()
            return
        }

        when (val c1 = stream.current) {
            '\\' -> {
                if (StringLiteralParser._parseEscape(stream, builder, false))
                    return
            }

            else -> {
                builder.rawData(c1)
            }
        }

        if (stream.move()) {
            builder.unclosedEof(stream.pos)
            return
        }

        when (stream.current) {
            '\'' -> {
                val quoteStart = stream.pos
                stream.move()
                builder.closingQuote(quoteStart, stream.pos)
                builder.finalize()
                return
            }

            '\n' -> {
                builder.unclosedEol(stream.pos)
                builder.finalize()
                return
            }
        }
    }


    private object OnelineStringRawDataPredicate : Predicate {
        override fun check(c: Char): Boolean {
            return c != '\\' && c != '\n' && c != '\"'
        }
    }

    private object MultilineStringRawDataPredicate : Predicate {
        override fun check(c: Char): Boolean {
            return c != '\"' && c != '\\'
        }
    }

    private suspend fun <P : Pos, CS : CollectedSubstring> _parseString(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder<P, CS>,
        isMultiline: Boolean
    ) {
        while (true) {
            if (stream.isEnded) {
                builder.unclosedEof(stream.pos)
                return
            }

            when (stream.current) {
                '\\' -> {
                    if (StringLiteralParser._parseEscape(stream, builder, isMultiline))
                        return
                }

                '\"' -> {
                    val quoteStart = stream.pos
                    stream.move()
                    builder.closingQuote(quoteStart, stream.pos)
                    builder.finalize()
                    return
                }

                '\n' -> if (!isMultiline) {
                    builder.unclosedEol(stream.pos)
                    builder.finalize()
                    return
                } else {
                    builder.rawData(stream.collect(MultilineStringRawDataPredicate))
                }

                else -> builder.rawData(
                    stream.collect(
                        if (isMultiline) MultilineStringRawDataPredicate else OnelineStringRawDataPredicate
                    )
                )
            }
        }
    }

    suspend fun <P : Pos, CS : CollectedSubstring> continueParsingOnelineString(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder.OnelineStringLiteralBuilder<P, CS>,
    ) {
        StringLiteralParser._parseString(stream, builder, false)
    }

    suspend fun <P : Pos, CS : CollectedSubstring> continueParsingMultilineString(
        stream: SourceStream<P, CS>,
        builder: StringLiteralBuilder.MultilineStringLiteralBuilder<P, CS>,
    ) {
        StringLiteralParser._parseString(stream, builder, true)
    }
}