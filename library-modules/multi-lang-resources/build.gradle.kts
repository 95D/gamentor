plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("dev.headwind.android.library")
}

android {
    namespace = "dev.headwind.multi.lang.resources"
}

dependencies {
}