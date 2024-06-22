package ru.landgrafhomyak.icpp.ast.builders

import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.HighlightOnly
import ru.landgrafhomyak.icpp.parser.environment.Pos

interface OnelineCommentFactory<P : Pos, CS : CollectedSubstring> {
    fun onelineComment(@HighlightOnly start: P, @HighlightOnly end: P)
}