plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Koin for dependency injection
    implementation(libs.koin.core)
}
