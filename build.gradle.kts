// Path: build.gradle.kts (Root Level)
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Yeh cloud runner ko batayega ki Android aur Kotlin build tools kahan se load karne hain
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
