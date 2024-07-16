import com.google.devtools.ksp.gradle.KspGradleSubplugin
import com.google.devtools.ksp.gradle.KspTaskMetadata
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
            kotlin.srcDir(buildDir.resolve("./generated/ksp/metadata/commonMain/kotlin/"))
            tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) }
        }
    }
//    tasks["compileKotlinAndroidNativeArm32"].dependsOn(tasks["kspCommonMainKotlinMetadata"])

}

dependencies {

//    println(configurations.joinToString {it.name})
//    configurations.forEach { c ->
//        if (!c.name.startsWith("ksp") || c.name == "ksp")
//            return@forEach
////        println(c.name)
//        add(c.name, project(":generator"))
//    }

    add("kspCommonMainMetadata", project(":generator"))
}
