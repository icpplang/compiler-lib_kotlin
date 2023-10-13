package io.github.landgrafhomyak.icpp.parser.test.environment.mockimpl

import io.github.landgrafhomyak.icpp.ast.builders.QualnameBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.CollectedSubstringTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.PosTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.ScopeAsserter


internal class QualnameAsserter(private val asserter: ScopeAsserter) : QualnameBuilder<CollectedSubstringTestImpl, PosTestImpl> {
    override fun addLevel(e: CollectedSubstringTestImpl) {
        this.asserter.addSubstr(QualnameBuilder<*, *>::addLevel, e)
    }

    override fun unfinishedScopeResolutionOperator(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameBuilder<*, *>::unfinishedScopeResolutionOperator, start, end)
    }

    override fun separator(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameBuilder<*, *>::separator, start, end)
    }

    override fun spacesBetweenSeparatorAndName(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, start, end)
    }

    override fun nothingAfterSeparator(p: PosTestImpl) {
        this.asserter.addPos(QualnameBuilder<*, *>::separator, p)
    }

    override fun addBadLevel(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameBuilder<*, *>::addBadLevel, start, end)
    }
}