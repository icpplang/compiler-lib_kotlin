package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.resume

internal fun runParserCoro(launcher: suspend () -> Unit) {
    class CompletionImpl : Continuation<Unit> {
        var finished = false
            private set
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            result.getOrThrow()
            this.finished = true
        }
    }

    val completion = CompletionImpl()
    launcher.createCoroutineUnintercepted(completion = completion).resume(Unit)
    if (!completion.finished)
        throw RuntimeException("Parser's coroutine uses suspension points by itself")
}