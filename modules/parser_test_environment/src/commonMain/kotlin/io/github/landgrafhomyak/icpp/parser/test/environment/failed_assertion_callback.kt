package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal interface FailedAssertionCallback {
    fun onPositionInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualPosKey: KFunction<*>, actualPos: Int)
    fun onPositionInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualPosKey: KFunction<*>, actualPos: Int)
    fun onInvalidPosKey(scope: ScopeAsserter, pos: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>)
    fun onInvalidPosValue(scope: ScopeAsserter, key: KFunction<*>, expectedPos: Int, actualPos: Int)
    fun onUnexpectedPos(scope: ScopeAsserter, actualPosKey: KFunction<*>, pos: Int)
    fun onRangeInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int)
    fun onRangeInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int)
    fun onInvalidRangeKey(scope: ScopeAsserter, start: Int, end: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>)
    fun onInvalidRangeStart(scope: ScopeAsserter, key: KFunction<*>, end: Int, expectedStart: Int, actualStart: Int)
    fun onInvalidRangeEnd(scope: ScopeAsserter, key: KFunction<*>, start: Int, expectedEnd: Int, actualEnd: Int)
    fun onUnexpectedRange(scope: ScopeAsserter, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int)
    fun onChildInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualChildKey: KClass<*>)
    fun onChildInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualChildKey: KClass<*>)
    fun onInvalidChildKey(scope: ScopeAsserter, expectedKey: KClass<*>, actualKey: KClass<*>)
    fun onUnexpectedChild(scope: ScopeAsserter, actualKey: KClass<*>)
}

internal class FailedAssertionOnlyFirstCallback(private val wrapped: FailedAssertionCallback) : FailedAssertionCallback {
    private var isCalled = false

    @OptIn(ExperimentalContracts::class)
    private inline fun check(call: () -> Unit) {
        contract {
            callsInPlace(call, InvocationKind.AT_MOST_ONCE)
        }

        if (this.isCalled)
            return
        this.isCalled = true
        call()
    }

    override fun onPositionInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualPosKey: KFunction<*>, actualPos: Int) = this.check {
        this.wrapped.onPositionInsteadOfChild(scope, expectedChildKey, actualPosKey, actualPos)
    }

    override fun onPositionInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualPosKey: KFunction<*>, actualPos: Int) = this.check {
        this.wrapped.onPositionInsteadOfRange(scope, expectedRangeKey, expectedStart, expectedEnd, actualPosKey, actualPos)
    }

    override fun onInvalidPosKey(scope: ScopeAsserter, pos: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>) = this.check {
        this.wrapped.onInvalidPosKey(scope, pos, expectedKey, actualKey)
    }

    override fun onInvalidPosValue(scope: ScopeAsserter, key: KFunction<*>, expectedPos: Int, actualPos: Int) = this.check {
        this.wrapped.onInvalidPosValue(scope, key, expectedPos, actualPos)
    }

    override fun onUnexpectedPos(scope: ScopeAsserter, actualPosKey: KFunction<*>, pos: Int) = this.check {
        this.wrapped.onUnexpectedPos(scope, actualPosKey, pos)
    }

    override fun onRangeInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.check {
        this.wrapped.onRangeInsteadOfPos(scope, expectedPosKey, expectedPos, actualRangeKey, actualStart, actualEnd)
    }

    override fun onRangeInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.check {
        this.wrapped.onRangeInsteadOfChild(scope, expectedChildKey, actualRangeKey, actualStart, actualEnd)
    }

    override fun onInvalidRangeKey(scope: ScopeAsserter, start: Int, end: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>) = this.check {
        this.wrapped.onInvalidRangeKey(scope, start, end, expectedKey, actualKey)
    }

    override fun onInvalidRangeStart(scope: ScopeAsserter, key: KFunction<*>, end: Int, expectedStart: Int, actualStart: Int) = this.check {
        this.wrapped.onInvalidRangeStart(scope, key, end, expectedStart, actualStart)
    }

    override fun onInvalidRangeEnd(scope: ScopeAsserter, key: KFunction<*>, start: Int, expectedEnd: Int, actualEnd: Int) = this.check {
        this.wrapped.onInvalidRangeEnd(scope, key, start, expectedEnd, actualEnd)
    }

    override fun onUnexpectedRange(scope: ScopeAsserter, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.check {
        this.wrapped.onUnexpectedRange(scope, actualRangeKey, actualStart, actualEnd)
    }

    override fun onChildInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualChildKey: KClass<*>) = this.check {
        this.wrapped.onChildInsteadOfPos(scope, expectedPosKey, expectedPos, actualChildKey)
    }

    override fun onChildInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualChildKey: KClass<*>) = this.check {
        this.wrapped.onChildInsteadOfRange(scope, expectedRangeKey, expectedStart, expectedEnd, actualChildKey)
    }

    override fun onInvalidChildKey(scope: ScopeAsserter, expectedKey: KClass<*>, actualKey: KClass<*>) = this.check {
        this.wrapped.onInvalidChildKey(scope, expectedKey, actualKey)
    }

    override fun onUnexpectedChild(scope: ScopeAsserter, actualKey: KClass<*>) = this.check {
        this.wrapped.onUnexpectedChild(scope, actualKey)
    }
}



