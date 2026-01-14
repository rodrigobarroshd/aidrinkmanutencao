plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.smartchip.aidrink.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.smartchip.aidrink.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,INDEX.LIST}"
            pickFirsts += setOf(
                "META-INF/io.netty.versions.properties"
            )
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
//    implementation(libs.compose.material3)
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation(libs.androidx.activity.compose)
    implementation("com.google.firebase:firebase-firestore:26.0.2")
    debugImplementation(libs.compose.ui.tooling)
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    // Lifecycle ViewModel + viewModelScope
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation ("androidx.datastore:datastore-preferences:1.1.1")
    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.2.0") // PreviewView
    implementation("com.google.guava:guava:31.1-android")
    // ML Kit Barcode
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
//    mqtt connection
    implementation("com.hivemq:hivemq-mqtt-client:1.3.3")
    implementation("com.rabbitmq:amqp-client:5.16.0")

    implementation("androidx.navigation:navigation-compose:2.7.3")
//    Gson
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
}