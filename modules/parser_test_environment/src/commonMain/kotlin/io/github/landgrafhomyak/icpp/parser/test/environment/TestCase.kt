package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.SourceStream

class TestCase<RootBuilder : Any> internal constructor(
    private val rootMock: RootBuilder,
    private val source: CharArray
) {
    private var isInvoked = false

    fun run(launcher: suspend (stream: SourceStream<CollectedSubstringTestImpl, PosTestImpl>, builder: RootBuilder) -> Unit) {
        if (this.isInvoked) throw IllegalStateException("This test already launched before")
        this.isInvoked = true

        runParserCoro {
            launcher(SourceStreamTestImpl(this@TestCase.source), this.rootMock)
        }
    }
}