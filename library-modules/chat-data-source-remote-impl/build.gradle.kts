plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("jp.co.nintendo.android.library")
}
android {
    namespace = "jp.co.nintendo.chat.data.source.remote.impl"
}

dependencies {
    implementation(project(":library-modules:chat-data-source-remote-api"))
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.timber)
    implementation(libs.bundles.network)
    implementation(libs.bundles.coroutines)
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
