package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.SourceStream
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest

class TestCase<RootBuilder : Any> internal constructor(
    private val rootMockFactory: () -> RootBuilder,
    private val source: CharArray
) {
    fun run(launcher: suspend (stream: SourceStream<CollectedSubstringTestImpl, PosTestImpl>, builder: RootBuilder) -> Unit): TestResult =
        runTest {
            launcher(SourceStreamTestImpl(this@TestCase.source), this@TestCase.rootMockFactory())
        }

    // fun void() {}
}