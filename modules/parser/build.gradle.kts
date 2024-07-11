//import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Scripts
//import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Versions

plugins {
    kotlin("multiplatform")
}
apply(from = rootDir.resolve("../_build_utilities/scripts/init_build.gradle.kts"))


kotlin {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly("ru.landgrafhomyak.icpp.compiler-lib:parser_environment")
                compileOnly("ru.landgrafhomyak.icpp.compiler-lib:ast_builders_abstract")
            }
        }

        val commonTest by getting {
            dependencies {
//                implementation(kotlin("test"))
//                implementation(project(":modules:parser_test_environment"))
            }
        }
    }
}