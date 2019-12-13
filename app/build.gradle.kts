import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.9.1")

    implementation("androidx.activity:activity:1.1.0-rc03")
    implementation("androidx.fragment:fragment:1.2.0-rc03")
    implementation("androidx.fragment:fragment-ktx:1.2.0-rc03")
    implementation("androidx.media:media:1.2.0-alpha01")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0-rc03")
    kapt("androidx.lifecycle:lifecycle-compiler:2.2.0-rc03")

    // KTX
    implementation("androidx.core:core:1.2.0-rc01")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.activity:activity-ktx:1.1.0-rc03")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.1.0")

    testImplementation("junit:junit:4.13-rc-2")
    testImplementation("org.mockito:mockito-core:3.0.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:1.3.61")
    implementation(project(":contract"))
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "info.anodsplace.headunit"
        minSdkVersion(16)
        targetSdkVersion(29)

        versionCode = 22
        versionName = "1.0"

        ndk {
            moduleName = "hu_jni"
            abiFilters("armeabi-v7a", "x86", "arm64-v8a", "x86_64")
        }

        externalNativeBuild {
            cmake {
                version = "3.10.2"
                arguments += "-DANDROID_TOOLCHAIN=clang"
            }
        }
    }

    externalNativeBuild {
        cmake {
            setPath("CMakeLists.txt")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file(property("HEADUNIT_KEYSTORE_FILE")!!)
            storePassword = property("HEADUNIT_KEYSTORE_PASSWORD") as String
            keyAlias = property("HEADUNIT_KEY_ALIAS") as String
            keyPassword = property("HEADUNIT_KEY_PASSWORD") as String
        }

        create("release") {
            storeFile = file(property("HEADUNIT_KEYSTORE_FILE")!!)
            storePassword = property("HEADUNIT_KEYSTORE_PASSWORD") as String
            keyAlias = property("HEADUNIT_KEY_ALIAS") as String
            keyPassword = property("HEADUNIT_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isDebuggable = true
            isJniDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    lintOptions {
        isAbortOnError = false
    }

    // task.genSource(type: Exec) {
    //     println "Executing " + HEADUNIT_SCRIPT_FILE
    //     commandLine HEADUNIT_SCRIPT_FILE
    // }

    // preBuild.dependsOn genSource

    // workaround for otlin tests
    // task copyTestClasses(type: Copy) {
    //     from "build/tmp/kotlin-classes/debugUnitTest"
    //     into "build/intermediates/classes/debug"
    // }
    // task copySdkClasses(type: Copy) {
    //     from "build/tmp/kotlin-classes/debug"
    //     into "build/intermediates/classes/debug"
    // }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        (this as KotlinJvmOptions).let {
           it.jvmTarget = "1.8"
        }
    }
}


