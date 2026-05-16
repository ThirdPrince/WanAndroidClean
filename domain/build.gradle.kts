plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // 修正：使用 api 确保 PagingData 类型在引用 domain 的 feature 模块中可见
    api(libs.androidx.paging.common)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Koin for dependency injection
    implementation(libs.koin.core)
}
