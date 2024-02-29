plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "es.ua.eps.filmoteca"
    compileSdk = 33

    defaultConfig {
        applicationId = "es.ua.eps.filmoteca"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //Properties properties = new Properties()
        //if(rootProject.file("local.properties").exists()){
        //    properties.load(rootProject.file("local.properties").newDataInputStream)
        //}

        //manifestPlaceholders = [mapsApiKey: properties.getProperty("MAPS_API_KEY", "")]
    }
    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.2")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /*Corrutinas*/
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    /*Dependencias para el firebase*/
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    implementation("com.google.firebase:firebase-analytics-ktx:21.5.1")

    testImplementation("junit:junit:4.13.2")
    /*Servicio de sign in (deprecado)*/
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    /* Dependencia necesarias para credentials manager */
    implementation("androidx.credentials:credentials:1.0.0-alpha02")
    implementation("androidx.credentials:credentials-play-services-auth:1.0.0-alpha02")
    /* Dependencia de google sign in*/
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    /* Glide for image */
    implementation("com.github.bumptech.glide:glide:4.14.2")

    /* Dependencias para los mapas */
    implementation("com.google.android.gms:play-services-maps:18.2.0")

}