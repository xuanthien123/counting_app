import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {
    object Compose {
        const val bom = "androidx.compose:compose-bom:${Versions.composeBom}"
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val preview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
        const val material3 = "androidx.compose.material3:material3:${Versions.composeMaterial3}"
//        const val navigation = "androidx.navigation:navigation-compose:${Versions.navigation}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
        const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata"
        const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.compose}"
    }

    object Hilt {
        const val android = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val compiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"
        const val navigation = "androidx.hilt:hilt-navigation-compose:1.2.0"
        const val hiltAgp = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    }

    object Room {
        const val runtime = "androidx.room:room-runtime:${Versions.room}"
        const val compiler = "androidx.room:room-compiler:${Versions.room}"
        const val ktx = "androidx.room:room-ktx:${Versions.room}"
    }

    object Network {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
        const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
        const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }

    object Coil {
        const val coil = "io.coil-kt:coil-compose:${Versions.coil}"
    }

    object Accompanist {
        const val systemUi = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
        const val navAnim = "com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist}"
    }

    object Debug {
        const val tooling = "androidx.compose.ui:ui-tooling"
        const val testManifest = "androidx.compose.ui:ui-test-manifest"
    }

    object Testing {
        const val junit4 = "androidx.compose.ui:ui-test-junit4:1.0.5"
    }
}

fun DependencyHandler.compose() {
    implementation(platform(Dependencies.Compose.bom))
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.preview)
    implementation(Dependencies.Compose.material3)
//    implementation(Dependencies.Compose.navigation)
    implementation(Dependencies.Compose.viewModel)
    implementation(Dependencies.Compose.runtimeLiveData)
    implementation(Dependencies.Compose.composeRuntime)
}

fun DependencyHandler.hilt() {
    implementation(Dependencies.Hilt.android)
    kapt(Dependencies.Hilt.compiler)
    implementation(Dependencies.Hilt.navigation)
}

fun DependencyHandler.room() {
    implementation(Dependencies.Room.runtime)
    kapt(Dependencies.Room.compiler)
    implementation(Dependencies.Room.ktx)
}

fun DependencyHandler.retrofit() {
    implementation(Dependencies.Network.retrofit)
    implementation(Dependencies.Network.moshiConverter)
    implementation(Dependencies.Network.okHttp)
    implementation(Dependencies.Network.okHttpLoggingInterceptor)
}

fun DependencyHandler.coroutines() {
    implementation(Dependencies.Coroutines.core)
    implementation(Dependencies.Coroutines.android)
}

fun DependencyHandler.coil() {
    implementation(Dependencies.Coil.coil)
}

fun DependencyHandler.accompanist() {
    implementation(Dependencies.Accompanist.systemUi)
    implementation(Dependencies.Accompanist.navAnim)
}

fun DependencyHandler.debug() {
    debugImplementation(Dependencies.Debug.tooling)
    debugImplementation(Dependencies.Debug.testManifest)
}

fun DependencyHandler.testing() {
//    androidTestImplementation(platform(Dependencies.Compose.bom))
    androidTestImplementation(Dependencies.Testing.junit4)
}