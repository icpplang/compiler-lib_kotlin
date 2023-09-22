package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.SourceStream

class TestCase<RootBuilder : Any> internal constructor(
    private val rootMockFactory: () -> RootBuilder,
    private val source: CharArray
) {
    fun run(launcher: suspend (stream: SourceStream<CollectedSubstringTestImpl, PosTestImpl>, builder: RootBuilder) -> Unit): TestCase<RootBuilder> {
        runParserCoro {
            launcher(SourceStreamTestImpl(this@TestCase.source), this@TestCase.rootMockFactory())
        }
        return this
    }

    fun void() {}
}