@file:JvmName("DslMultiplatformKt")

package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass

object ParserTest {
    fun <RootBuilder : Any> build(
        rootBuilder: KClass<RootBuilder>,
        builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder> {
        val sourceBuilder = StringBuilder()
        val expectedEntities = EntityArrayList()
        val builderReceiver = TestCaseBuilderImpl<RootBuilder>(sourceBuilder, expectedEntities)
        builder(builderReceiver)

        val source = sourceBuilder.toString().toCharArray()
        val callbacks = ScheduledThrowOnFailedAssertionCallback(ThrowOnFailedAssertionCallback(source))
        val asserter = ScopeAsserterImpl(rootBuilder, FailedAssertionOnlyFirstCallback(callbacks), expectedEntities)
        return TestCase(mock(rootBuilder, asserter), source)
    }

    inline fun <reified RootBuilder : Any> build(
        noinline builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder> = build(RootBuilder::class, builder)
}


internal expect fun <T : Any> mock(i: KClass<T>, meta: ScopeAsserter): T