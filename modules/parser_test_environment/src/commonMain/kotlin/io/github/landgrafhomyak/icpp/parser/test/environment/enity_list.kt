package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal interface EntityList : Iterable<EntityList.Entity> {
    sealed class Entity
    class PosEntity(
        val key: KFunction<*>,
        val pos: Int
    ) : Entity() {
        override fun toString(): String = "<position ${this.pos} with key ${this.key}>"
    }

    class RangeEntity(
        val key: KFunction<*>,
        val start: Int,
        val end: Int
    ) : Entity() {
        override fun toString(): String = "<range ${this.start}:${this.end} with key ${this.key}>"
    }

    class ChildEntity(
        val key: KClass<*>,
        val entities: EntityList
    ) : Entity() {
        override fun toString(): String = "<child scope with key ${this.key}>"
    }

    override fun iterator(): Iterator<Entity>

    fun flatIterator() = this.iterator().flatten()
}

internal interface MutableEntityList : EntityList {
    fun add(key: KFunction<*>, pos1: Int)

    fun add(key: KFunction<*>, pos1: PosTestImpl) = this.add(key, pos1.value)

    fun add(key: KFunction<*>, pos1: Int, pos2: Int)

    fun add(key: KFunction<*>, pos1: PosTestImpl, pos2: PosTestImpl) = this.add(key, pos1.value, pos2.value)

    fun add(key: KClass<*>, subEntities: EntityList)
}

@JvmInline
internal value class EntityArrayList private constructor(private val data: ArrayList<EntityList.Entity>) : MutableEntityList {
    constructor() : this(arrayListOf())

    override fun add(key: KFunction<*>, pos1: Int) {
        this.data.add(EntityList.PosEntity(key, pos1))
    }

    override fun add(key: KFunction<*>, pos1: Int, pos2: Int) {
        this.data.add(EntityList.RangeEntity(key, pos1, pos2))
    }

    override fun add(key: KClass<*>, subEntities: EntityList) {
        this.data.add(EntityList.ChildEntity(key, subEntities))
    }

    override fun iterator(): Iterator<EntityList.Entity> = this.data.iterator()
}

private class EntityListFlatIterator(root: Iterator<EntityList.Entity>) : Iterator<EntityList.Entity> {
    private val stack = arrayListOf(root)

    private fun collectFinishedIterators() {
        while (this.stack.lastOrNull()?.hasNext() == false)
            this.stack.removeLast()
    }

    override fun hasNext(): Boolean {
        this.collectFinishedIterators()
        return this.stack.isNotEmpty()
    }

    /**
     * Next function
     *
     * @author xomrk
     * @return next item
     * @throws IllegalStateException no more items
     */
    override fun next(): EntityList.Entity {
        if (!this.hasNext())
            throw IllegalStateException("no more items")
        return this.stack.last().next()
    }
}

internal fun Iterator<EntityList.Entity>.flatten(): Iterator<EntityList.Entity> = EntityListFlatIterator(this)

internal object EmptyEntityList : EntityList {
    private object EmptyIterator : Iterator<EntityList.Entity> {
        override fun hasNext(): Boolean = false

        override fun next(): EntityList.Entity = throw IllegalStateException("no more entities")
    }

    override fun iterator(): Iterator<EntityList.Entity> = EmptyIterator

    override fun flatIterator(): Iterator<EntityList.Entity> = EmptyIterator
}