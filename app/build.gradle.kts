plugins { 
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android { 
    namespace = "com.offlineplanner"
    compileSdk = 34
    
    defaultConfig { 
        applicationId = "com.offlineplanner"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"
        vectorDrawables.useSupportLibrary = true
    }
    
    buildTypes { 
        release { 
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { 
            isMinifyEnabled = false
        }
    }
    
    buildFeatures { 
        compose = true
    }
    
    composeOptions { 
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    
    kotlinOptions { 
        jvmTarget = "17"
    }
    
    packaging { 
        resources { 
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}