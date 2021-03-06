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

dependencies {
    compileOnly("org.jetbrains:annotations:21.0.1")
}

intellij {
    version.set(findProperty("idea.version").toString())
}