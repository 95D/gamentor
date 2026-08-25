plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("dev.headwind.android.library")
}

android {
    namespace = "dev.headwind.chat.data.source.local.impl"
}

dependencies {

    implementation(project(":library-modules:chat-domain-api"))
    implementation(project(":library-modules:chat-data-source-local-api"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.room)
    implementation(libs.timber)
    ksp(libs.androidx.room.compiler)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.paging)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.room.testing)
}

