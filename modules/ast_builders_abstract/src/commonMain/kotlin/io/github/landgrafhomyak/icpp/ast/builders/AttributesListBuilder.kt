package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.HighlightOnly
import io.github.landgrafhomyak.icpp.parser.environment.Error
import io.github.landgrafhomyak.icpp.parser.environment.Pos


interface AttributesListBuilder<CS : CollectedSubstring, P : Pos> {
    @Error
    fun unclosed(pos: P)

    @Error
    fun unsupportedSymbols(start: P, end: P)

    @HighlightOnly
    fun comma(pos: P)
}