package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal interface ScopeAsserter {
    val key: KClass<*>
    fun addPos(actualKey: KFunction<*>, pos: PosTestImpl)
    fun addRange(actualKey: KFunction<*>, start: PosTestImpl, end: PosTestImpl)
    fun getChildAsserter(actualKey: KClass<*>): ScopeAsserter
}

internal class ScopeAsserterImpl(
    override val key: KClass<*>,
    private val callbacks: FailedAssertionCallback,
    private val expectedEntities: EntityList,
    private val actualEntities: MutableEntityList = EntityArrayList()
) : ScopeAsserter {
    private val expectedFlow = this.expectedEntities.iterator()

    private fun nextExpectedEntityOrNull(): EntityList.Entity? =
        if (this.expectedFlow.hasNext()) this.expectedFlow.next()
        else null


    override fun addPos(actualKey: KFunction<*>, pos: PosTestImpl) {
        this.actualEntities.add(actualKey, pos)
        when (val e = this.nextExpectedEntityOrNull()) {
            is EntityList.PosEntity -> {
                if (e.key != actualKey) this.callbacks.onInvalidPosKey(this, e.pos, actualKey, e.key)
                if (e.pos != pos.value) this.callbacks.onInvalidPosValue(this, e.key, pos.value, e.pos)
            }

            is EntityList.RangeEntity -> this.callbacks.onPositionInsteadOfRange(this, e.key, e.start, e.end, actualKey, pos.value)
            is EntityList.ChildEntity -> this.callbacks.onPositionInsteadOfChild(this, e.key, actualKey, pos.value)
            null -> this.callbacks.onUnexpectedPos(this, actualKey, pos.value)
        }
    }

    override fun addRange(actualKey: KFunction<*>, start: PosTestImpl, end: PosTestImpl) {
        this.actualEntities.add(actualKey, start, end)
        when (val e = this.nextExpectedEntityOrNull()) {
            is EntityList.PosEntity -> this.callbacks.onRangeInsteadOfPos(this, e.key, e.pos, actualKey, start.value, end.value)
            is EntityList.RangeEntity -> {
                if (e.key != actualKey) this.callbacks.onInvalidRangeKey(this, start.value, end.value, e.key, actualKey)
                if (e.start != start.value) this.callbacks.onInvalidRangeStart(this, e.key, e.end, e.start, start.value)
                if (e.end != end.value) this.callbacks.onInvalidRangeEnd(this, e.key, e.start, e.end, end.value)
            }

            is EntityList.ChildEntity -> this.callbacks.onRangeInsteadOfChild(this, e.key, actualKey, start.value, end.value)
            null -> this.callbacks.onUnexpectedRange(this, actualKey, start.value, end.value)
        }
    }

    override fun getChildAsserter(actualKey: KClass<*>): ScopeAsserter {
        val childEntities = EntityArrayList()
        this.actualEntities.add(actualKey, childEntities)
        val expectedEntities: EntityList
        when (val e = this.nextExpectedEntityOrNull()) {
            is EntityList.PosEntity -> {
                expectedEntities = EmptyEntityList
                this.callbacks.onChildInsteadOfPos(this, e.key, e.pos, actualKey)
            }

            is EntityList.RangeEntity -> {
                expectedEntities = EmptyEntityList
                this.callbacks.onChildInsteadOfRange(this, e.key, e.start, e.end, actualKey)
            }

            is EntityList.ChildEntity -> {
                if (e.key == actualKey) {
                    expectedEntities = e.entities
                } else {
                    expectedEntities = EmptyEntityList
                    this.callbacks.onInvalidChildKey(this, e.key, actualKey)
                }
            }

            null -> {
                expectedEntities = EmptyEntityList
                this.callbacks.onUnexpectedChild(this, actualKey)
            }
        }

        return ScopeAsserterImpl(actualKey, this.callbacks, expectedEntities)
    }
}
















