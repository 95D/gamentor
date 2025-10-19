plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("jp.co.nintendo.android.library")
}

android {
    namespace = "jp.co.nintendo.app.di"
}

dependencies {
    implementation(project(":library-modules:chat-ui-impl"))
    implementation(project(":library-modules:automation-business-logic-impl"))
    implementation(project(":library-modules:setting-ui-impl"))
    implementation(project(":library-modules:setting-android-impl"))
    implementation(project(":library-modules:setting-data-repository-impl"))
    implementation(project(":library-modules:setting-data-source-local-impl"))
    implementation(project(":library-modules:chat-data-repository-impl"))
    implementation(project(":library-modules:chat-data-source-local-impl"))
    implementation(project(":library-modules:chat-data-source-remote-impl"))
    implementation(project(":library-modules:id-infra-impl"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}