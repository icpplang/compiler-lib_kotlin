package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KFunction

@Suppress("EqualsOrHashCode")
internal class EntityList {
    private var first: Entity? = null
    private var last: Entity? = null
    private var size: Int = 0

    fun add(key: KFunction<*>, pos1: Int) {
        val n = Entity(key, pos1, null)
        this.last?.apply { next = n } ?: run { this.first = n }
        this.last = n
        this.size++
    }

    fun add(key: KFunction<*>, pos1: Int, pos2: Int) {
        val n = Entity(key, pos1, pos2)
        this.last?.apply { next = n } ?: run { this.first = n }
        this.last = n
        this.size++
    }

    fun add(key: KFunction<*>, pos1: PosTestImpl) {
        val n = Entity(key, pos1.value, null)
        this.last?.apply { next = n } ?: run { this.first = n }
        this.last = n
        this.size++
    }

    fun add(key: KFunction<*>, pos1: PosTestImpl, pos2: PosTestImpl) {
        val n = Entity(key, pos1.value, pos2.value)
        this.last?.apply { next = n } ?: run { this.first = n }
        this.last = n
        this.size++
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EntityList) return false
        if (this.size != other.size)
            return false

        var tP = this.first
        var oP = other.first

        while (tP != null && oP != null) {
            if (tP.key != oP.key)
                return false
            if (tP.pos1 != oP.pos1)
                return false
            if (tP.pos2 != oP.pos2)
                return false

            tP = tP.next
            oP = oP.next
        }

        return tP == null && oP == null
    }

    private class Entity(
        val key: KFunction<*>,
        val pos1: Int,
        val pos2: Int?
    ) {
        var next: Entity? = null
    }

    companion object {
        @JvmStatic
        fun assertEquals(expected: EntityList, actual: EntityList) {
            assert(expected.size == actual.size) { "Entities list size differs: expected ${expected.size} entities, got ${actual.size}" }

            var eP = expected.first
            var aP = actual.first
            var i = 0

            while (eP != null && aP != null) {
                assert(eP.key != aP.key) { "Entity #${i} key differs: expected ${eP!!.key}, got ${aP!!.key}" }
                assert(eP.pos1 != aP.pos1) { "Entity #${i} first pos differs: expected ${eP!!.pos1}, got ${aP!!.pos1}" }
                assert(eP.pos2 != aP.pos2) { "Entity #${i} second pos differs: expected ${eP!!.pos2}, got ${aP!!.pos2}" }

                i++
                eP = eP.next
                aP = aP.next
            }
        }
    }
}