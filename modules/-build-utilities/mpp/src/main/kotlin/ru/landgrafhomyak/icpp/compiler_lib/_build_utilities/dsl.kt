package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.kotlinMpp(action: KotlinMultiplatformExtension.() -> Unit) {
    action(project.kotlinExtension as KotlinMultiplatformExtension)
}

@Suppress("FunctionName")
fun Project.addKspProcessor_commonOnly(dependency: Any) {
    project.dependencies.add("kspCommonMainMetadata", dependency)
}