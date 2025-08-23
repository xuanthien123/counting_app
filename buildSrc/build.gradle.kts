plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    implementation("com.android.tools.build:gradle:8.11.1")
    implementation("com.squareup:javapoet:1.13.0")
//    implementation("org.jetbrains.kotlin.plugin.compose:2.1.21")
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