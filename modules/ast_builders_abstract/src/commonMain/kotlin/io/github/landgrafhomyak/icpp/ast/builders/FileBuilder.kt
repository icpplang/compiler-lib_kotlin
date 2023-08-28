package io.github.landgrafhomyak.icpp.ast.builders

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Error
import io.github.landgrafhomyak.icpp.parser.environment.Pos

interface FileBuilder<CS : CollectedSubstring, P : Pos> {
    @Error
    fun unsupportedTopLevelSymbols(start: P, end: P)

    @Error
    fun missedNewLineBeforeDeclaration(declarationStart: P)

    @Error
    fun unknownKeyword(kw: CS)
}