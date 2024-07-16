import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Libs
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.kotlinMpp

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("ru.landgrafhomyak.icpp.compiler-lib:-build-utilities/mpp")
    }
}

repositories {
    mavenCentral()
}

apply<InitBuild>()

kotlinMpp {
    jvm {
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Modules.named("parser/environment"))
                implementation(project(":mock-annotation"))
                implementation(Libs.KSP_API)
                implementation(Libs.KOTLIN_POET)
                implementation(Libs.KOTLIN_POET_KSP_INTEROP)
            }
        }
    }
}