@file:JvmName("DslMultiplatformKt")

package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass

actual object ParserTest {
    actual fun <RootBuilder : Any> buildParserTest(
        rootBuilder: KClass<RootBuilder>,
        builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder> {
        val sb = StringBuilder()
        val expectData = Scope(rootBuilder)
        val scope = TestCaseBuilderImpl<RootBuilder>(sb, expectData)
        builder(scope)
        return TestCase(InterfaceMapping.constructor(rootBuilder), sb.toString().toCharArray())
    }

    actual inline fun <reified RootBuilder : Any> buildParserTest(
        noinline builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder> = buildParserTest(RootBuilder::class, builder)
}
