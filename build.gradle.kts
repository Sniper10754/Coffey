plugins {
    kotlin("jvm") version "1.5.21"
	java
}

repositories {
    mavenCentral()
}

allprojects {
    group = "org.coffey"
    version = "0.2"
    description = "Packet Manager"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
