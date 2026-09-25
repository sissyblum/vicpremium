plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.victorstudio.victorpremium"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.victorstudio.victorpremium"
        minSdk = 21          // celulares viejos + TV boxes viejas
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // dejar en false hasta validar todo; luego activar R8
        }
    }
}

dependencies {
    // --- Media3 / ExoPlayer: núcleo + todos los formatos que necesita IPTV ---
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.1")   // MPD / DASH
    implementation("androidx.media3:media3-exoplayer-hls:1.5.1")    // m3u8 / HLS
    implementation("androidx.media3:media3-exoplayer-rtsp:1.5.1")   // por si hace falta
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")               // PlayerView + controles
    implementation("androidx.media3:media3-session:1.5.1")          // MediaSession (TV, notif., control remoto)
    implementation("androidx.media3:media3-datasource-okhttp:1.5.1")
    // NOTA sobre ffmpeg / dav1d / IAMF / MPEG-H:
    // estos NO son artefactos de Maven Central. Media3 los distribuye como
    // módulos para compilar desde el propio repo (androidx/media3, carpetas
    // libraries/decoder_ffmpeg, decoder_av1, decoder_iamf, decoder_mpegh),
    // usando NDK. Ver el README que dejo en el proyecto (EXTENSIONES_DECODER.md)
    // con los pasos exactos para agregarlos cuando tengas Android Studio/red.

    // --- Networking (igual que Víctor Play: Retrofit contra los JSON) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // --- Firebase (lista de servidores en vivo) ---
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // --- UI estándar ---
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("com.github.bumptech.glide:glide:4.16.0") // logos de canales

    // --- Android TV (leanback: banners, soporte D-pad) ---
    implementation("androidx.leanback:leanback:1.0.0")

    testImplementation("junit:junit:4.13.2")
}
