plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {}
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation(project(":modules:parser_environment"))
                implementation(project(":modules:ast_builders_abstract"))

            }
        }
        val manualMock by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(manualMock)
            dependencies {
            }
        }
    }
}