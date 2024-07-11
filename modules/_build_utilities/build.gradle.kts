plugins {
    kotlin("jvm") version embeddedKotlinVersion
//    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.gradle.org/gradle/libs-releases-local/")
    flatDir {
        name = "internal"
        dirs(projectDir.resolve("./../../m2-repo/"))
    }
}

val gradleApiVersion = "6.1.1"

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                compileOnly("org.gradle:gradle-core:$gradleApiVersion")
                compileOnly("org.gradle:gradle-core-api:$gradleApiVersion")
                compileOnly("org.gradle:gradle-kotlin-dsl:$gradleApiVersion")
            }
        }
    }
}

//
//
//
//publishing {
//    repositories {
//        maven {
//            name = "internal"
//            url = uri(projectDir.resolve("./../../m2-repo/"))
//        }
//    }
//    publications {
//        create<MavenPublication>("internal") {
//            groupId = "ru.landgrafhomyak.icpp.compiler-lib"
//            artifactId = "_build_utilities"
//            version = "-"
//
//            from(components["java"])
//        }
//    }
//}