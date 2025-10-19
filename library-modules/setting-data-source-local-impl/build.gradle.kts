plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("jp.co.nintendo.android.library")
}

android {
    namespace = "jp.co.nintendo.setting.data.source.local.impl"
}

dependencies {
    implementation(project(":library-modules:setting-domain-api"))
    implementation(project(":library-modules:setting-data-source-local-api"))
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.timber)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.paging)
    implementation(libs.datastore)
    implementation(libs.datastore.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.junit)
}