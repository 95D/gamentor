plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "jp.co.nintendo.android.application"
            implementationClass = "AndroidAppConventionPlugin"
        }
    }
    plugins {
        register("androidLibrary") {
            id = "jp.co.nintendo.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
