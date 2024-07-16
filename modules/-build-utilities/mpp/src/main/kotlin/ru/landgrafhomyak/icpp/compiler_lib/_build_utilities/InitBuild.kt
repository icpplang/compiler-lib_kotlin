package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

import org.gradle.api.Plugin
import org.gradle.api.Project

class InitBuild : Plugin<Project> {
    override fun apply(project: Project) {
        project.group = Modules.GROUP_NAME
        project.plugins.apply("org.jetbrains.kotlin.multiplatform")
        project.kotlinMpp {
            jvm()
//            js(IR) { // errors with composite build
////                nodejs()
//                browser()
//            }
            androidNativeArm32()
             androidNativeArm64()
            androidNativeX86()
            androidNativeX64()
            iosArm64()
            iosSimulatorArm64()
            watchosArm32()
            watchosArm64()
            watchosX64()
            iosX64()
            watchosSimulatorArm64()
            mingwX64()
            macosX64()
            macosArm64()
            linuxX64()
            linuxArm64()
        }
    }
}