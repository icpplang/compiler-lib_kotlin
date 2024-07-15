import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.kotlinMpp


buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("ru.landgrafhomyak.icpp.compiler-lib:_build_utilities/mpp")
    }
}

repositories {
    mavenCentral()
}

apply<InitBuild>()

kotlinMpp {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(Modules.named("parser/environment"))
                compileOnly(Modules.named("parser/ast_builders"))
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