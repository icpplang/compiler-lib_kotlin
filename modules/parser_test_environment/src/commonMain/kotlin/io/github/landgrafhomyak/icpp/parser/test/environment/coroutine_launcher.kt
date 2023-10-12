package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.resume

@OptIn(ExperimentalContracts::class)
fun <T> runParserTestCoro(launcher: suspend () -> T): T {
    contract {
        callsInPlace(launcher, InvocationKind.EXACTLY_ONCE)
    }

    class CompletionImpl : Continuation<T> {
        var finished = false
            private set
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        var result: T? = null
            private set

        override fun resumeWith(result: Result<T>) {
            this.result = result.getOrThrow()
            this.finished = true
        }
    }

    val completion = CompletionImpl()
    launcher.createCoroutineUnintercepted(completion = completion).resume(Unit)
    if (!completion.finished)
        throw RuntimeException("Parser's coroutine uses suspension points by itself")

    @Suppress("UNCHECKED_CAST")
    return completion.result as T
}