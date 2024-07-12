import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.kotlin

apply<InitBuild>()

kotlin {
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