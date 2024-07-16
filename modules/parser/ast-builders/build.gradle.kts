import com.google.devtools.ksp.gradle.KspGradleSubplugin
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
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

plugins {
//    id("com.google.devtools.ksp")
    idea
}

repositories {
    mavenCentral()
}

apply<InitBuild>()
apply<KspGradleSubplugin>()

kotlinMpp {
    jvm {
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(Modules.named("parser/environment"))
                api(project(":mock-annotation"))
            }
        }
    }
}

dependencies {
    configurations.forEach { c ->
        if (!c.name.startsWith("ksp") || c.name == "ksp")
            return@forEach
        add(c.name, project(":generator"))
    }
}
