package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

fun Project.kotlinJvm(action: KotlinJvmProjectExtension.() -> Unit) {
    action(project.kotlinExtension as KotlinJvmProjectExtension)
}