@file:JvmName("DslMultiplatformKt")

package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass

expect object ParserTest {
    fun <RootBuilder : Any> buildParserTest(
        rootBuilder: KClass<RootBuilder>,
        builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder>

    inline fun <reified RootBuilder : Any> buildParserTest(
        noinline builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder> //= buildParserTest(RootBuilder::class, builder)
}


typealias _P = PosTestImpl
typealias _CS = CollectedSubstringTestImpl