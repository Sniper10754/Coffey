plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.beust:klaxon:5.5")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.9")
}

java {
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_9
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_9
}
