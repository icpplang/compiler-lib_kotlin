package ru.landgrafhomyak.icpp.ast.builders

import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Error
import ru.landgrafhomyak.icpp.parser.environment.Finalizer
import ru.landgrafhomyak.icpp.parser.environment.HighlightOnly
import ru.landgrafhomyak.icpp.parser.environment.Pos

interface MultilineCommentBuilder<P : Pos, CS : CollectedSubstring> {
    interface MultilineCommentFactory<P : Pos, CS : CollectedSubstring> {
        fun multilineComment(@HighlightOnly openingSequenceStart: P/*, @HighlightOnly openingSequenceEnd: P*/): NumericLiteralBuilder<P, CS>

    }

    /*
    fun addInnerCommentLevel(@HighlightOnly openingSequenceStart: P, @HighlightOnly openingSequenceEnd: P)
    fun removeInnerCommentLevel(@HighlightOnly closingSequenceStart: P, @HighlightOnly closingSequenceEnd: P)
    */

    fun closingSequence(/* @HighlightOnly closingSequenceStart: P, */@HighlightOnly closingSequenceEnd: P)

    @Finalizer
    fun finalize()

    @Error
    fun unclosedComment()

}