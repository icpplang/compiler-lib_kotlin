plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":modules:parser_environment"))
            }
        }
    }
}