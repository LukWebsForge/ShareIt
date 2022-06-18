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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
}

intellij {
    version.set(findProperty("idea.version").toString())
}