import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Firma de release: `keystore.properties` no se commitea (ver .gitignore) — cuando no existe
// (p.ej. un clon nuevo del repo sin la keystore) el build de release simplemente sale sin firmar
// en vez de fallar. En CI, el workflow reconstruye ambos archivos a partir de secrets antes de
// compilar.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
val hasReleaseKeystore = keystorePropertiesFile.exists()
if (hasReleaseKeystore) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.PolGrauDev.reproductor_nativo_android"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.PolGrauDev.reproductor_nativo_android"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Nombre de archivo del APK de release para distribución (GitHub Releases) — el de debug se
// deja con el nombre por defecto (`app-debug.apk`), que es el que usan el README y los comandos
// `adb install` documentados. Renombrar vía una tarea posterior al empaquetado en vez de la
// Variant API de AGP: más estable entre versiones que depender de un método concreto que puede
// cambiar de nombre/firma según la versión de AGP.
val renameReleaseApk = tasks.register("renameReleaseApk") {
    val releaseDir = layout.buildDirectory.dir("outputs/apk/release")
    doLast {
        val dir = releaseDir.get().asFile
        val original = dir.listFiles { f -> f.name.endsWith(".apk") && f.name != "Reproductor Add Free.apk" }
        original?.forEach { it.renameTo(File(dir, "Reproductor Add Free.apk")) }
    }
}

afterEvaluate {
    tasks.named("assembleRelease") {
        finalizedBy(renameReleaseApk)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Media3 / ExoPlayer — reproducción y sesión multimedia
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Carga de imágenes (portadas de álbum) — Coil 3
    implementation(libs.coil.compose)

    // ViewModel + lifecycle con Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navegación entre pantallas
    implementation(libs.androidx.navigation.compose)

    // Persistencia (favoritos y playlists)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Persistencia de ajustes (temporizador, fundido)
    implementation(libs.androidx.datastore.preferences)
}