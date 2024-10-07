// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("org.sonarqube") version "4.4.1.3373"
    //id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.gms) apply false
}


sonar {
    properties {
        property("sonar.projectKey", "BookswapEPFL_Bookswap")
        property("sonar.organization", "bookswapepfl")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}