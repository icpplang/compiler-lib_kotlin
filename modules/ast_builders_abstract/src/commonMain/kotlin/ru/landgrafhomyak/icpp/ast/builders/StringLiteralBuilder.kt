package ru.landgrafhomyak.icpp.ast.builders

import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Error
import ru.landgrafhomyak.icpp.parser.environment.Finalizer
import ru.landgrafhomyak.icpp.parser.environment.Pos

interface StringLiteralBuilder<P : Pos, CS : CollectedSubstring> {
    interface StringLiteralFactory<P : Pos, CS : CollectedSubstring> {
        fun onelineStringLiteralOpeningQuote(start: P, end: P): StringLiteralBuilder<P, CS>
        fun multilineStringLiteralOpeningQuote(start: P, end: P): StringLiteralBuilder<P, CS>
        fun charLiteralOpeningQuote(start: P, end: P): StringLiteralBuilder<P, CS>
    }

    fun rawData(cs: CS)
    fun rawData(c: Char)

    fun closingQuote(start: P, end: P)


    enum class SpecialChar {
        NEW_LINE, TAB, NULL, BACK_SLASH, SINGLE_QUOTE, DOUBLE_QUOTE
    }

    fun escapeSequenceSpecialChar(escapeSequenceStart: P, valueStart: P, valueEnd: P, value: SpecialChar)

    fun escapeSequenceC8(escapeSequenceStart: P, valueStart: P, valueEnd: P, value: UByte)

    fun escapeSequenceC16(escapeSequenceStart: P, valueStart: P, valueEnd: P, value: UShort)

    fun escapeSequenceC32(escapeSequenceStart: P, valueStart: P, valueEnd: P, value: UInt)

    @Finalizer
    fun finalize()

    @Error
    fun unclosedEof(at: P)

    @Error
    fun unclosedEol(at: P)

    @Error
    fun unknownEscapeSequenceSpecialChar(escapeSequenceStart: P, valueStart: P, valueEnd: P, value: Char)

    @Error
    fun unfinishedEscapeSequenceSpecialChar(escapeSequenceStart: P, valueStart: P, valueEnd: P)

    @Error
    fun unfinishedEscapeSequenceCharCode(escapeSequenceStart: P, valueStart: P, valueEnd: P)

    interface MultilineStringLiteralBuilder<P : Pos, CS : CollectedSubstring> : StringLiteralBuilder<P, CS>
    interface OnelineStringLiteralBuilder<P : Pos, CS : CollectedSubstring> : StringLiteralBuilder<P, CS>
    interface CharLiteralBuilder<P : Pos, CS : CollectedSubstring> : StringLiteralBuilder<P, CS>
}