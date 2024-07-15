package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class InitBuild : Plugin<Project> {
    override fun apply(project: Project) {
        project.group = Modules.GROUP_NAME
        project.plugins.apply(KotlinPluginWrapper::class.java)
    }
}