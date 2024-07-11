plugins {
    kotlin("multiplatform")
}

apply(from=rootDir.resolve("../_build_utilities/scripts/init_build.gradle.kts"))

kotlin {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly("ru.landgrafhomyak.icpp.compiler-lib:parser_environment")
            }
        }
    }
}