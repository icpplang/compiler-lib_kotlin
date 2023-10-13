package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.QualnameBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.ParserTest
import io.github.landgrafhomyak.icpp.parser.test.environment.TestCase
import io.github.landgrafhomyak.icpp.parser.test.environment.TestCaseBuilder
import kotlin.test.Test

class QualnameTest {
    private fun TestCase<QualnameBuilder<*, *>>.runQualname() =
        this.run(IdentifierParsers::parseQualname)

    private fun ParserTest.buildQualnameTest(builder: TestCaseBuilder<QualnameBuilder<*, *>>.() -> Unit) =
        this.build(builder)

    @Test
    fun testValidOneLevel() = ParserTest
        .buildQualnameTest {
            substr(QualnameBuilder<*, *>::addLevel, "134")
        }
        .runQualname()

    @Test
    fun testValidOneLevelWithSpaces() = ParserTest
        .buildQualnameTest {
            substr(QualnameBuilder<*, *>::addLevel, "134")
            noise("     ")
        }
        .runQualname()
}