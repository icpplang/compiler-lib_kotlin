import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.kotlinMpp

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("ru.landgrafhomyak.icpp.compiler-lib:_build_utilities_mpp")
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
                compileOnly(kotlin("reflect"))
                compileOnly(Modules.named("parser_environment"))
            }
        }
    }
}