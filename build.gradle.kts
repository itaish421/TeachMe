// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20-RC2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {

    dependencies {
        val navVersion = "2.8.0"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}
