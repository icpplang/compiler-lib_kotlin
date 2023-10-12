plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":modules:parser_environment"))
                implementation(project(":modules:ast_builders_abstract"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":modules:parser_test_environment"))
            }
        }
    }
}