plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("jp.co.nintendo.android.library")
}

android {
    namespace = "jp.co.nintendo.chat.domain.impl"
}

dependencies {
    implementation(project(":library-modules:automation-domain-api"))
    implementation(project(":library-modules:chat-domain-api"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.timber)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
