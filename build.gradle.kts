// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false

    // Required for Nav 3 Type-Safe Navigation
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    alias(libs.plugins.kotlin.android) apply false
}