@file:Suppress("ObjectPropertyName", "MemberVisibilityCanBePrivate", "unused")

import org.gradle.api.JavaVersion

object Versions {
    const val kotlin = "1.3.61"
    const val gradle_tools = "3.5.2"

    // Supported SDK versions
    const val sdkMin = 16
    const val sdkTarget = 29
    const val sdkCompile = sdkTarget

    // Application version
    const val appVersionCode = 1
    const val appVersionName = "1.0"

    val java = JavaVersion.VERSION_1_8

    // AndroidX
    object AndroidX {
        const val appcompat = "1.1.0"
        const val coreKtx = "1.2.0-rc01"
        const val constraintLayout = "1.1.3"
        const val coordinatorLayout = "1.1.0-rc01"
        const val recyclerView = "1.1.0"
        const val lifecycle = "2.1.0"
        const val navigation = "2.1.0"
    }
    const val material = "1.2.0-alpha02"

    // Dagger
    const val dagger = "2.25.2"

    // ReactiveX
    const val rxJava = "2.2.15"
    const val rxAndroid = "2.1.1"
    const val rxKotlin = "2.4.0"

    // Networking
    object OkHttp {
        const val latest = "4.2.2"
        const val logger = "3.10.0"
    }

    const val retrofit = "2.6.2"

    // Testing
    const val junit = "4.12"
    const val mockito = "3.2.0"
    const val robolectric = "4.3"

    object Instrumentation {
        const val junit = "1.1.0"
        const val core = "1.2.0"
        const val coreKtx = "1.1.0"
        const val fragment = "1.1.0"
        const val espresso = "3.2.0"
    }

    // Google Auto
    const val autoService = "1.0-rc6"
}

object Dependencies {

    const val gradle = "com.android.tools.build:gradle:${Versions.gradle_tools}"

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    // AndroidX
    const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.AndroidX.coreKtx}"
    const val coordinatorLayout = "androidx.coordinatorlayout:coordinatorlayout:${Versions.AndroidX.coordinatorLayout}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
    const val navigation = "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigation}"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.AndroidX.lifecycle}"
    const val liveData = "androidx.lifecycle:lifecycle-livedata:${Versions.AndroidX.lifecycle}"
    const val viewModelExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.AndroidX.lifecycle}"
    const val material = "com.google.android.material:material:${Versions.material}"

    // Dagger
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerProcessor = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    // RxJava
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin}"

    // Networking
    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp.latest}"
    const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.OkHttp.logger}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitRxJava = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    // Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val mockito = "org.mockito:mockito-inline:${Versions.mockito}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val lifecycleTest = "androidx.arch.core:core-testing:${Versions.AndroidX.lifecycle}"

    const val androidXJunit = "androidx.test.ext:junit:${Versions.Instrumentation.junit}"
    const val androidXEspresso = "androidx.test.espresso:espresso-core:${Versions.Instrumentation.espresso}"
    const val androidXCoreKtx = "androidx.test:core-ktx:${Versions.Instrumentation.core}"
    const val androidXCore = "androidx.test:core:${Versions.Instrumentation.core}"
    const val androidXFragment = "androidx.fragment:fragment-testing:${Versions.Instrumentation.fragment}"

    // Google Auto
    const val autoServiceAnnotations = "com.google.auto.service:auto-service-annotations:${Versions.autoService}"
    const val autoService = "com.google.auto.service:auto-service:${Versions.autoService}"
}