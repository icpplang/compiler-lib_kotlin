import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.EnableKsp_CommonOnly
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.kotlinMpp
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.addKspProcessor_commonOnly


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
apply<EnableKsp_CommonOnly>()

addKspProcessor_commonOnly(project(":generator"))

kotlinMpp {
    sourceSets {
        commonMain {
            dependencies {
                compileOnly(Modules.named("parser/environment"))
                api(project(":mock-annotation"))
            }
        }
    }
}
