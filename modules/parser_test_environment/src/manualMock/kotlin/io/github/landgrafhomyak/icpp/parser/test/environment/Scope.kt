package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

internal open class Scope(
    private val key: KClass<*>,
    internal val entities: EntityList,
    internal val children: MutableList<Scope>
) {
    constructor(key: KClass<*>) : this(key, EntityList(), ArrayList<Scope>())

    companion object {
        @JvmStatic
        fun assert(expected: Scope, actual: Scope) {

        }
    }
}