package ru.landgrafhomyak.icpp.parser.test.environment.mockimpl

import io.github.landgrafhomyak.icpp.ast.builders.QualnameUsageBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.CollectedSubstringTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.PosTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.ScopeAsserter


internal class QualnameAsserter(private val asserter: ScopeAsserter) : QualnameUsageBuilder<CollectedSubstringTestImpl, PosTestImpl> {
    override fun addLevel(e: CollectedSubstringTestImpl) {
        this.asserter.addSubstr(QualnameUsageBuilder<*, *>::addLevel, e)
    }

    override fun unfinishedScopeResolutionOperator(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameUsageBuilder<*, *>::unfinishedScopeResolutionOperator, start, end)
    }

    override fun scopeResolutionOperator(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameUsageBuilder<*, *>::scopeResolutionOperator, start, end)
    }

    override fun spacesBetweenSeparatorAndName(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, start, end)
    }

    override fun nothingAfterSeparator(p: PosTestImpl) {
        this.asserter.addPos(QualnameUsageBuilder<*, *>::nothingAfterSeparator, p)
    }

    override fun addBadLevel(start: PosTestImpl, end: PosTestImpl) {
        this.asserter.addRange(QualnameUsageBuilder<*, *>::addBadLevel, start, end)
    }
}