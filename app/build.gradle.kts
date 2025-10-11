plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("jp.co.nintendo.android.application")
}

android {
    namespace = "jp.co.nintendo.gamentor"

    defaultConfig {
        applicationId = "jp.co.nintendo.gamentor"
    }
}

dependencies {
    implementation(project(":library-modules:chat-ui-api"))
    implementation(project(":library-modules:design-system"))
    implementation(project(":library-modules:app-di"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
