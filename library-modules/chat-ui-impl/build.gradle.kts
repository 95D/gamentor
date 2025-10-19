plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("jp.co.nintendo.android.library")
}

android {
    namespace = "jp.co.nintendo.chat.ui.impl"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":library-modules:ui-core-compose"))
    implementation(project(":library-modules:chat-ui-entry-api"))
    implementation(project(":library-modules:setting-ui-entry-api"))
    implementation(project(":library-modules:chat-domain-api"))
    implementation(project(":library-modules:automation-domain-api"))
    implementation(project(":library-modules:setting-domain-api"))
    implementation(project(":library-modules:multi-lang-resources"))
    implementation(project(":library-modules:design-system"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.bundles.adaptive.layout)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.paging)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
