package ru.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.QualnameUsageBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.CollectedSubstringTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.ParserTest
import io.github.landgrafhomyak.icpp.parser.test.environment.PosTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.TestCaseBuilder
import kotlin.test.Test

class QualnameTest {
    private fun ParserTest.runQualnameTest(consumeSpaces: Boolean, consumeInvalidIdentifier: Boolean, testBuilder: TestCaseBuilder<QualnameUsageBuilder<*, *>>.() -> Unit) =
        this
            .build(testBuilder)
            .run<QualnameUsageBuilder<CollectedSubstringTestImpl, PosTestImpl>> { stream, builder ->
                ru.landgrafhomyak.icpp.parser.NameUsageParser.parseQualname(
                    stream,
                    builder,
                    consumeSpaces = consumeSpaces,
                    consumeInvalidIdentifier = consumeInvalidIdentifier
                )
            }

    @Test
    fun testValidOneLevel() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
        }

    @Test
    fun testValidTwoLevels() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            substr(QualnameUsageBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testValidTwoLevelsWithSpacesOff1() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            +" "
            +ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR
            +"def"
        }

    @Test
    fun testValidTwoLevelsWithSpacesOff2() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            pos(QualnameUsageBuilder<*, *>::nothingAfterSeparator)
            +" "
            +"def"
        }

    @Test
    fun testValidTwoLevelsWithSpacesOn1() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            substr(QualnameUsageBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testValidTwoLevelsWithSpacesOn2() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            substr(QualnameUsageBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testValidTwoLevelsWithSpacesOn3() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            substr(QualnameUsageBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testInvalidSecondOff() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            pos(QualnameUsageBuilder<*, *>::nothingAfterSeparator)
            +"0def"
        }

    @Test
    fun testInvalidSecondOn() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            range(QualnameUsageBuilder<*, *>::addBadLevel, "0def")
        }

    @Test
    fun testInvalidSecondOnWithSpacesOff1() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            +" "
            +ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR
            +"0def"
        }

    @Test
    fun testInvalidSecondOnWithSpacesOff2() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            pos(QualnameUsageBuilder<*, *>::nothingAfterSeparator)
            +" "
            +"0def"
        }

    @Test
    fun testInvalidSecondOnWithSpacesOff3() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            +" "
            +ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR
            +" "
            +"0def"
        }

    @Test
    fun testInvalidSecondOnWithSpacesOn1() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            range(QualnameUsageBuilder<*, *>::addBadLevel, "0def")
        }

    @Test
    fun testInvalidSecondOnWithSpacesOn2() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameUsageBuilder<*, *>::addBadLevel, "0def")
        }

    @Test
    fun testInvalidSecondOnWithSpacesOn3() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = true) {
            substr(QualnameUsageBuilder<*, *>::addLevel, "abc")
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameUsageBuilder<*, *>::scopeResolutionOperator, ru.landgrafhomyak.icpp.parser.NameUsageParser.SCOPE_RESOLUTION_OPERATOR)
            range(QualnameUsageBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameUsageBuilder<*, *>::addBadLevel, "0def")
        }
    @Test
    fun testOneInvalid() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            range(QualnameUsageBuilder<*, *>::addBadLevel, "0abc")
        }
}
