@file:JvmName("DslMultiplatformKt")

package ru.landgrafhomyak.icpp.parser.test.environment

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
        return TestCase(mock(rootBuilder, asserter), source, callbacks::assert)
    }

    inline fun <reified RootBuilder : Any> build(
        noinline builder: TestCaseBuilder<RootBuilder>.() -> Unit
    ): TestCase<RootBuilder> = build(RootBuilder::class, builder)
}


internal expect fun <T : Any> mock(i: KClass<T>, meta: ScopeAsserter): T
private inline fun <R> CharArray.split(c: Char, start: Int, end: Int, consume: (s: Int, e: Int) -> R) {
    var s = 0
    for (i in start until end) {
        if (this@split[i] == c) {
            consume(s, i)
            s = i + 1
        }
    }
    if (s < this@split.size)
        consume(s, this@split.size)
}

fun formatTestSourceRange(source: CharArray, start: Int, end: Int): String {
    val lineStart = run brk@{ source.foldIndexed(0) fold@{ i, b, c -> if (i > start) return@brk b else return@fold if (c == '\n') i + 1 else b } }
    val lineEnd = run brk@{ source.foldRightIndexed(source.size) fold@{ i, c, b -> if (i < end) return@brk b else return@fold if (c == '\n') i else b } }
    val out = StringBuilder()
    source.split('\n', lineStart, lineEnd) { s, e ->
        out.append("|> ")
        out.appendRange(source, s, s + e)
        out.append("\n|  ")
        val ss: Int
        if (s < start) {
            ss = start
            out.append(" ".repeat(start - s))
        } else {
            ss = s
        }
        if (end < e) {
            out.append("^".repeat(end - ss))
            out.append(" ".repeat(e - end))
        } else {
            out.append("^".repeat(e - ss))
            if (e == source.size && end > source.size) {
                out.append("^".repeat(end - e))
            }
        }
        out.append('\n')
    }
    // out.append('\n')
    return out.toString()
}