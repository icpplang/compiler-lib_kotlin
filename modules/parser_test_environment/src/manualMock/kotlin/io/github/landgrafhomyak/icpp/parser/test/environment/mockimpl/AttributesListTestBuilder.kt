package io.github.landgrafhomyak.icpp.parser.test.environment.mockimpl

import io.github.landgrafhomyak.icpp.ast.builders.AttributesListBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.CollectedSubstringTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.PosTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.Scope


internal class AttributesListTestBuilder : Scope(AttributesListBuilder::class), AttributesListBuilder<CollectedSubstringTestImpl, PosTestImpl> {
    override fun unclosed(pos: PosTestImpl) {
        this.entities.add(AttributesListTestBuilder::unclosed, pos)
    }

    override fun unsupportedSymbols(start: PosTestImpl, end: PosTestImpl) {
        this.entities.add(AttributesListTestBuilder::unsupportedSymbols, start, end)
    }

    override fun comma(pos: PosTestImpl) {
        this.entities.add(AttributesListTestBuilder::comma, pos)
    }
}