plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
