package ru.landgrafhomyak.icpp.parser.test.environment

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.instanceParameter

@Suppress("ClassName")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class FailedAssertionCallback_NeedTest

internal interface FailedAssertionCallback {
    @FailedAssertionCallback_NeedTest
    fun onPositionInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualPosKey: KFunction<*>, actualPos: Int)

    @FailedAssertionCallback_NeedTest
    fun onPositionInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualPosKey: KFunction<*>, actualPos: Int)

    @FailedAssertionCallback_NeedTest
    fun onInvalidPosKey(scope: ScopeAsserter, pos: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>)

    @FailedAssertionCallback_NeedTest
    fun onInvalidPosValue(scope: ScopeAsserter, key: KFunction<*>, expectedPos: Int, actualPos: Int)

    @FailedAssertionCallback_NeedTest
    fun onUnexpectedPos(scope: ScopeAsserter, key: KFunction<*>, pos: Int)

    @FailedAssertionCallback_NeedTest
    fun onRangeInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int)

    @FailedAssertionCallback_NeedTest
    fun onRangeInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int)

    @FailedAssertionCallback_NeedTest
    fun onInvalidRangeKey(scope: ScopeAsserter, start: Int, end: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>)

    @FailedAssertionCallback_NeedTest
    fun onInvalidRangeStart(scope: ScopeAsserter, key: KFunction<*>, end: Int, expectedStart: Int, actualStart: Int)

    @FailedAssertionCallback_NeedTest
    fun onInvalidRangeEnd(scope: ScopeAsserter, key: KFunction<*>, start: Int, expectedEnd: Int, actualEnd: Int)

    @FailedAssertionCallback_NeedTest
    fun onUnexpectedRange(scope: ScopeAsserter, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int)

    @FailedAssertionCallback_NeedTest
    fun onChildInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualChildKey: KClass<*>)

    @FailedAssertionCallback_NeedTest
    fun onChildInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualChildKey: KClass<*>)

    @FailedAssertionCallback_NeedTest
    fun onInvalidChildKey(scope: ScopeAsserter, expectedKey: KClass<*>, actualKey: KClass<*>)

    @FailedAssertionCallback_NeedTest
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

    override fun onUnexpectedPos(scope: ScopeAsserter, key: KFunction<*>, pos: Int) = this.check {
        this.wrapped.onUnexpectedPos(scope, key, pos)
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

internal class ThrowOnFailedAssertionCallback(private val source: CharArray) : FailedAssertionCallback {
    private val KClass<*>.key: String
        get() = this
            .qualifiedName!!
            .let { k -> "`${k}`" }

    private val KFunction<*>.key: String
        get() = this
            .instanceParameter!!
            .type
            .classifier!!
            .let { c -> c as KClass<*> }
            .qualifiedName!!
            .let { c -> "${c}#${this.name}" }
            .let { k -> "`${k}`" }

    private inline fun format(scope: ScopeAsserter, builder: () -> String): Nothing = throw AssertionError("\n[scope ${scope.key.key}]\n" + builder())

    override fun onPositionInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualPosKey: KFunction<*>, actualPos: Int) = this.format(scope) {
        "Expected child scope with key ${expectedChildKey.key}, got position with key ${actualPosKey.key}:\n${
            formatTestSourceRange(this.source, actualPos, actualPos + 1)
        }"
    }

    override fun onPositionInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualPosKey: KFunction<*>, actualPos: Int) = this.format(scope) {
        "Expected range with key ${expectedRangeKey.key}, got position with key ${actualPosKey.key}:\nExpected:\n${
            formatTestSourceRange(this.source, actualPos, actualPos + 1)
        }\n\nActual:\n${
            formatTestSourceRange(this.source, expectedStart, expectedEnd)
        }"
    }


    override fun onInvalidPosKey(scope: ScopeAsserter, pos: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>) = this.format(scope) {
        "Expected position with key ${expectedKey.key}, got ${actualKey.key}:\n${
            formatTestSourceRange(this.source, pos, pos + 1)
        }"
    }

    override fun onInvalidPosValue(scope: ScopeAsserter, key: KFunction<*>, expectedPos: Int, actualPos: Int) = this.format(scope) {
        "Invalid position value with key ${key.key}:\nExpected:\n${
            formatTestSourceRange(this.source, expectedPos, expectedPos + 1)
        }\n\nActual:\n${
            formatTestSourceRange(this.source, actualPos, actualPos + 1)
        }"
    }

    override fun onUnexpectedPos(scope: ScopeAsserter, key: KFunction<*>, pos: Int) = this.format(scope) {
        "Unexpected position with key ${key.key}:\n${
            formatTestSourceRange(this.source, pos, pos + 1)
        }"
    }

    override fun onRangeInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.format(scope) {
        "Expected position with key ${expectedPosKey.key}, got range with key ${actualRangeKey.key}:\nExpected\n${
            formatTestSourceRange(this.source, expectedPos, expectedPos + 1)
        }\n\nActual:\n${
            formatTestSourceRange(this.source, actualStart, actualEnd)
        }"
    }

    override fun onRangeInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.format(scope) {
        "Expected child scope with key ${expectedChildKey.key}, got range with key ${actualRangeKey.key}:\n${
            formatTestSourceRange(this.source, actualStart, actualEnd)
        }"
    }

    override fun onInvalidRangeKey(scope: ScopeAsserter, start: Int, end: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>) = this.format(scope) {
        "Expected range with key ${expectedKey.key}, got key ${actualKey.key}:\n${
            formatTestSourceRange(this.source, start, end)
        }"
    }

    override fun onInvalidRangeStart(scope: ScopeAsserter, key: KFunction<*>, end: Int, expectedStart: Int, actualStart: Int) = this.format(scope) {
        "Range with key ${key.key} with invalid start\nExpected:\n${
            formatTestSourceRange(this.source, expectedStart, end)
        }\n\nActual:\n${
            formatTestSourceRange(this.source, actualStart, end)
        }"
    }

    override fun onInvalidRangeEnd(scope: ScopeAsserter, key: KFunction<*>, start: Int, expectedEnd: Int, actualEnd: Int) = this.format(scope) {
        "Range with key ${key.key} with invalid end\nExpected:\n${
            formatTestSourceRange(this.source, start, expectedEnd)
        }\n\nActual:\n${
            formatTestSourceRange(this.source, start, actualEnd)
        }"
    }

    override fun onUnexpectedRange(scope: ScopeAsserter, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.format(scope) {
        "Unexpected range with key ${actualRangeKey.key}:\n${
            formatTestSourceRange(this.source, actualStart, actualEnd)
        }"
    }

    override fun onChildInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualChildKey: KClass<*>) = this.format(scope) {
        "Expected position with key ${expectedPosKey.key}, got child scope with key ${actualChildKey.key}:\n${
            formatTestSourceRange(this.source, expectedPos, expectedPos + 1)
        }"
    }

    override fun onChildInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualChildKey: KClass<*>) = this.format(scope) {
        "Expected range with key ${expectedRangeKey.key}, got child scope with key ${actualChildKey.key}:\n${
            formatTestSourceRange(this.source, expectedStart, expectedEnd)
        }"
    }

    override fun onInvalidChildKey(scope: ScopeAsserter, expectedKey: KClass<*>, actualKey: KClass<*>) = this.format(scope) {
        "Expected child scope with key ${expectedKey.key}, got with key ${actualKey.key}"
    }


    override fun onUnexpectedChild(scope: ScopeAsserter, actualKey: KClass<*>) = this.format(scope) {
        "Unexpected child scope with key ${actualKey.key}"
    }
}

internal class ScheduledThrowOnFailedAssertionCallback(private val wrapped: FailedAssertionCallback) : FailedAssertionCallback {
    private var err: AssertionError? = null

    fun assert() {
        this.err?.also { e -> throw e }
    }

    private inline fun capture(fn: () -> Unit) {
        try {
            fn()
        } catch (a: AssertionError) {
            this.err = a
        }
    }

    override fun onPositionInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualPosKey: KFunction<*>, actualPos: Int) = this.capture {
        this.wrapped.onPositionInsteadOfChild(scope, expectedChildKey, actualPosKey, actualPos)
    }

    override fun onPositionInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualPosKey: KFunction<*>, actualPos: Int) = this.capture {
        this.wrapped.onPositionInsteadOfRange(scope, expectedRangeKey, expectedStart, expectedEnd, actualPosKey, actualPos)
    }

    override fun onInvalidPosKey(scope: ScopeAsserter, pos: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>) = this.capture {
        this.wrapped.onInvalidPosKey(scope, pos, expectedKey, actualKey)
    }

    override fun onInvalidPosValue(scope: ScopeAsserter, key: KFunction<*>, expectedPos: Int, actualPos: Int) = this.capture {
        this.wrapped.onInvalidPosValue(scope, key, expectedPos, actualPos)
    }

    override fun onUnexpectedPos(scope: ScopeAsserter, key: KFunction<*>, pos: Int) = this.capture {
        this.wrapped.onUnexpectedPos(scope, key, pos)
    }

    override fun onRangeInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.capture {
        this.wrapped.onRangeInsteadOfPos(scope, expectedPosKey, expectedPos, actualRangeKey, actualStart, actualEnd)
    }

    override fun onRangeInsteadOfChild(scope: ScopeAsserter, expectedChildKey: KClass<*>, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.capture {
        this.wrapped.onRangeInsteadOfChild(scope, expectedChildKey, actualRangeKey, actualStart, actualEnd)
    }

    override fun onInvalidRangeKey(scope: ScopeAsserter, start: Int, end: Int, expectedKey: KFunction<*>, actualKey: KFunction<*>) = this.capture {
        this.wrapped.onInvalidRangeKey(scope, start, end, expectedKey, actualKey)
    }

    override fun onInvalidRangeStart(scope: ScopeAsserter, key: KFunction<*>, end: Int, expectedStart: Int, actualStart: Int) = this.capture {
        this.wrapped.onInvalidRangeStart(scope, key, end, expectedStart, actualStart)
    }

    override fun onInvalidRangeEnd(scope: ScopeAsserter, key: KFunction<*>, start: Int, expectedEnd: Int, actualEnd: Int) = this.capture {
        this.wrapped.onInvalidRangeEnd(scope, key, start, expectedEnd, actualEnd)
    }

    override fun onUnexpectedRange(scope: ScopeAsserter, actualRangeKey: KFunction<*>, actualStart: Int, actualEnd: Int) = this.capture {
        this.wrapped.onUnexpectedRange(scope, actualRangeKey, actualStart, actualEnd)
    }

    override fun onChildInsteadOfPos(scope: ScopeAsserter, expectedPosKey: KFunction<*>, expectedPos: Int, actualChildKey: KClass<*>) = this.capture {
        this.wrapped.onChildInsteadOfPos(scope, expectedPosKey, expectedPos, actualChildKey)
    }

    override fun onChildInsteadOfRange(scope: ScopeAsserter, expectedRangeKey: KFunction<*>, expectedStart: Int, expectedEnd: Int, actualChildKey: KClass<*>) = this.capture {
        this.wrapped.onChildInsteadOfRange(scope, expectedRangeKey, expectedStart, expectedEnd, actualChildKey)
    }

    override fun onInvalidChildKey(scope: ScopeAsserter, expectedKey: KClass<*>, actualKey: KClass<*>) = this.capture {
        this.wrapped.onInvalidChildKey(scope, expectedKey, actualKey)
    }

    override fun onUnexpectedChild(scope: ScopeAsserter, actualKey: KClass<*>) = this.capture {
        this.wrapped.onUnexpectedChild(scope, actualKey)
    }
}