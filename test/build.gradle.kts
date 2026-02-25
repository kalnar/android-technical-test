plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
