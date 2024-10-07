version = "1.0-SNAPSHOT"
group = "de.lukweb.share"

plugins {
    java
    idea
    id("org.jetbrains.intellij.platform")
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly("org.jetbrains:annotations:25.0.0")
    intellijPlatform {
        intellijIdeaCommunity(findProperty("idea.version").toString())
        instrumentationTools()
    }
}
