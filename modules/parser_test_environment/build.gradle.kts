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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation(project(":modules:parser_environment"))
                implementation(project(":modules:ast_builders_abstract"))

            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("reflect"))
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

        val commonJvmTest by creating {
            dependsOn(commonTest)
        }

        val jvmTest by getting {
            dependsOn(commonJvmTest)
        }
    }
}