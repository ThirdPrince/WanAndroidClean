plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Koin for dependency injection
    implementation(libs.koin.core)
}
