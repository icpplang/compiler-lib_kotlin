
package ru.landgrafhomyak.icpp.compiler_lib._build_utilities

import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("ClassName")
class EnableKsp_CommonOnly : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("com.google.devtools.ksp")

        project.kotlinMpp {
            sourceSets.commonMain {
                // kotlin.srcDir(project.buildDir.resolve("./generated/ksp/metadata/commonMain/kotlin/"))

                // magic from https://github.com/google/ksp/issues/963#issuecomment-1894144639
                project.tasks.withType(KspTaskMetadata::class.java) { t ->
                    kotlin.srcDir(t.destinationDirectory)
                }
            }
        }
    }
}