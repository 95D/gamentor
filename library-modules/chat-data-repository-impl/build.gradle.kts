plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("jp.co.nintendo.android.library")
}

android {
    namespace = "jp.co.nintendo.chat.data.repository.impl"
}

dependencies {
    implementation(project(":library-modules:automation-domain-api"))
    implementation(project(":library-modules:chat-domain-api"))
    implementation(project(":library-modules:chat-data-source-local-api"))
    implementation(project(":library-modules:chat-data-source-remote-api"))

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.timber)
    implementation(libs.bundles.room)
    add("ksp", libs.androidx.room.compiler)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.paging)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.room.testing)
}
