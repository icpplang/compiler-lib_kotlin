package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.SourceStream
import kotlin.reflect.KClass

class TestCase<RootBuilder : Any> internal constructor(
    private val rootMock: RootBuilder,
    private val source: CharArray
) {
    private var isInvoked = false

    fun <RootBuilderImpl : RootBuilder> run(
        builderClass: KClass<RootBuilderImpl>,
        launcher: suspend (stream: SourceStream<CollectedSubstringTestImpl, PosTestImpl>, builder: RootBuilderImpl) -> Unit
    ) {
        if (this.isInvoked) throw IllegalStateException("This test already launched before")
        this.isInvoked = true

        if (!builderClass.isInstance(this.rootMock))
            throw IllegalArgumentException("You should provide to runner same interface as to builder")

        runParserCoro {
            @Suppress("UNCHECKED_CAST")
            launcher(SourceStreamTestImpl(this@TestCase.source), this.rootMock as RootBuilderImpl)
        }
    }

    inline fun <reified RootBuilderImpl : RootBuilder> run(
        noinline launcher: suspend (stream: SourceStream<CollectedSubstringTestImpl, PosTestImpl>, builder: RootBuilderImpl) -> Unit
    ) = this.run(RootBuilderImpl::class, launcher)
}