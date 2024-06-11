@file:OptIn(ExperimentalContracts::class)

package ru.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.ast.builders.QualnameUsageBuilder
import io.github.landgrafhomyak.icpp.ast.builders._GroupedElementsEnumerationBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun TestCaseBuilder<QualnameUsageBuilder<*, *>>.addTemplatedLevel(
    name: String,
    templateBuilder: TestCaseBuilder<_GroupedElementsEnumerationBuilder<*, *>>.() -> Unit
) {
    contract { callsInPlace(templateBuilder, InvocationKind.EXACTLY_ONCE) }
    this.substr(QualnameUsageBuilder<*, *>::addTemplatedLevel, name)
    this.subObject<_GroupedElementsEnumerationBuilder<*, *>>(templateBuilder)
}