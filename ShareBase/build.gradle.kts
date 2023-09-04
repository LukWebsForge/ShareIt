version = "1.0-SNAPSHOT"
group = "de.lukweb.share"

plugins {
    java
    idea
    id("org.jetbrains.intellij")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
}

intellij {
    version.set(findProperty("idea.version").toString())
}