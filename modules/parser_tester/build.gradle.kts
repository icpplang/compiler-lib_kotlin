buildscript {
    allprojects {
        buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath("ru.landgrafhomyak.icpp.compiler-lib:_build_utilities")
            }
        }

        repositories {
            mavenCentral()
        }
    }

}