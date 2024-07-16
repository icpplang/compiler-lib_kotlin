package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

object Libs {
    @JvmStatic
    val KSP_API = "com.google.devtools.ksp:symbol-processing-api:${Versions.KSP}"

    @JvmStatic
    val KOTLIN_POET = "com.squareup:kotlinpoet:${Versions.KOTLIN_POET}"

    @JvmStatic
    val KOTLIN_POET_KSP_INTEROP = "com.squareup:kotlinpoet-ksp:${Versions.KOTLIN_POET}"
}