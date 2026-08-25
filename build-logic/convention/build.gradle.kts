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
            id = "dev.headwind.android.application"
            implementationClass = "AndroidAppConventionPlugin"
        }
    }
    plugins {
        register("androidLibrary") {
            id = "dev.headwind.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
