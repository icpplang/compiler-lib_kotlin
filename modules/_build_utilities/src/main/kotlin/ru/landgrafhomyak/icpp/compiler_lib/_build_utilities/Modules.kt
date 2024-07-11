package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

object Modules {
    @JvmStatic
    val GROUP_NAME = "ru.landgrafhomyak.icpp.compiler-lib"

    fun named(name: String): Any = "${this.GROUP_NAME}:${name}"
}