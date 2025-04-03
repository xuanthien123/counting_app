plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
    implementation("com.android.tools.build:gradle:8.1.3")
    implementation("com.squareup:javapoet:1.13.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17)) // Hoặc 21 nếu cần
}

kotlin {
    jvmToolchain(17) // Nếu đang chạy Java 23
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}