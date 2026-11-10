import java.util.Properties

plugins {
    id("com.android.application")
}

val signingProps = Properties()
val signingPropsFile = rootProject.file("signing.properties")
if (signingPropsFile.exists()) {
    signingProps.load(signingPropsFile.inputStream())
}

android {
    namespace = "com.zydark.dfroot"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.zydark.dfroot"
        minSdk = 32
        targetSdk = 37
        versionCode = 407
        versionName = "4.7"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        if (signingPropsFile.exists()) {
            create("release") {
                storeFile = file(signingProps.getProperty("KEYSTORE_FILE"))
                storePassword = signingProps.getProperty("KEYSTORE_PASSWORD")
                keyAlias = signingProps.getProperty("KEY_ALIAS")
                keyPassword = signingProps.getProperty("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            // If signing.properties exists, use release signing; otherwise fall back to debug signing
            signingConfig = if (signingPropsFile.exists())
                signingConfigs.getByName("release")
            else
                signingConfigs.getByName("debug")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkReleaseBuilds = false
    }

    externalNativeBuild {
        cmake {
            path("src/main/jni/CMakeLists.txt")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set(output.versionName.map { vn -> "ZYDARK_DFRoot_${vn}.apk" })
        }
    }
}

dependencies {
}
