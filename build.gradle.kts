plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlinx" && requested.name.startsWith("kotlinx-serialization")) {
                    useVersion("1.6.3")
                    because("Force version 1.6.3 to maintain compatibility with Kotlin 1.9.22 and remove InternalSerializationApi errors")
                }
            }
        }
    }
}
