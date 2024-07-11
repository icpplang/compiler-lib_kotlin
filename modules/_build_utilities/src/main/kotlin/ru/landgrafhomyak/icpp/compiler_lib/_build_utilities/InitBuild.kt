package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

class InitBuild : Plugin<Project> {
    override fun apply(project: Project) {
        project.group = Modules.GROUP_NAME
        project.plugins.apply(KotlinMultiplatformPluginWrapper::class.java)
//        project.kotlin {  configKotlin() }
    }

    private fun KotlinMultiplatformExtension.configKotlin() {
        jvm {}
    }
}