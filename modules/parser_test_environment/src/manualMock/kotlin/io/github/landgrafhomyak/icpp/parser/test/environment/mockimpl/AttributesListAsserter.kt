package io.github.landgrafhomyak.icpp.parser.test.environment.mockimpl

import io.github.landgrafhomyak.icpp.ast.builders.AttributesListBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.CollectedSubstringTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.PosTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.ScopeAsserter


internal class AttributesListAsserter(private val asserter: ScopeAsserter) : AttributesListBuilder<CollectedSubstringTestImpl, PosTestImpl> {
    override fun unclosed(pos: PosTestImpl) {
        this.asserter.addPos(AttributesListBuilder<*, *>::unclosed, pos)
    }

    override fun unsupportedSymbols(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(AttributesListBuilder<*, *>::unsupportedSymbols, start, end)
    }

    override fun comma(pos: PosTestImpl) {
        this.asserter.addPos(AttributesListBuilder<*, *>::comma, pos)
    }
}