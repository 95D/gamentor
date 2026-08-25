plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("dev.headwind.android.application")
}

android {
    namespace = "dev.headwind.gamentor"

    defaultConfig {
        applicationId = "dev.headwind.gamentor"
    }
}

dependencies {
    implementation(project(":library-modules:chat-ui-entry-api"))
    implementation(project(":library-modules:setting-ui-entry-api"))
    implementation(project(":library-modules:setting-domain-api"))
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
    implementation(libs.bundles.adaptive.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
