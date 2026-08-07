import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.secrets)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.ruqyah.eysosm"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: file("${rootDir}/my-upload-key.jks").absolutePath
            
            storeFile = file(keystorePath)
            storePassword = System.getenv("STORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: "upload"
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""

            // تأكد من وجود الملف قبل الاستخدام
            if (!storeFile.exists()) {
                logger.warn("⚠️ Keystore file not found at: $keystorePath")
            }
        }
        
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            // إضافة لدعم R8
            ndk.debugSymbolLevel = "FULL"
        }
        
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debugConfig")
            isDebuggable = true
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
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = true
    }
}

// Configure the Secrets Gradle Plugin
secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
    ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

// يجب أن يكون ERROR وليس WARN
googleServices {
    missingGoogleServicesStrategy = MissingGoogleServicesStrategy.ERROR
}

dependencies {
    // BOM Platforms
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Firebase
    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.recaptcha)
    // implementation(libs.firebase.firestore) // أزل التعليق عند الحاجة

    // Google Sign-In & Drive Sync
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    ksp(libs.moshi.kotlin.codegen)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.androidx.compose.ui.test.junit4)

    // Android Test
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// تحسينات إضافية
androidComponents {
    onVariants(selector().all()) { variant ->
        // تعطيل معالجة PNG للسلالات release فقط
        if (variant.buildType == "release") {
            variant.androidResources.aaptOptions.additionalParameters += "--no-crunch"
        }
    }
}
