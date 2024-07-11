plugins {
    kotlin("jvm") version embeddedKotlinVersion
//    `maven-publish`

}

repositories {
    mavenCentral()
//    maven("https://repo.gradle.org/gradle/libs-releases-local/")
}
group = "ru.landgrafhomyak.icpp.compiler-lib"

val gradleApiVersion = gradle.gradleVersion

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                compileOnly("dev.gradleplugins:gradle-api:${gradle.gradleVersion}")
                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootDir.resolve("./../../kotlin-version.txt").readText()}")
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