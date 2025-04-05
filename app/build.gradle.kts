plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Thêm dòng này
    id("dagger.hilt.android.plugin") // Cần thiết cho Hilt
}

android {
    namespace = "com.aquarina.countingapp"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        applicationId = ProjectConfig.appId
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("key-app.jks")  // Đường dẫn tới keystore
            storePassword = "thien123"  // Mật khẩu của keystore
            keyAlias = "key-app"  // Tên alias của key
            keyPassword = "thien123"  // Mật khẩu của key
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")  // Áp dụng signing config cho release
            isMinifyEnabled = false  // Tắt ProGuard/R8 nếu không muốn tối ưu hóa mã
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true // Bật Jetpack Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    compose()
    hilt()
    room()
    retrofit()
    coroutines()
    coil()
    accompanist()
    debug()
    testing()
}