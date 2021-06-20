import org.jetbrains.changelog.markdownToHTML

version = "1.20.0"
group = "de.lukweb.share"

plugins {
    java
    idea
    id("org.jetbrains.intellij")
    id("org.jetbrains.changelog")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    implementation(project(":ShareBase"))
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
}

intellij {
    version.set(findProperty("idea.version").toString())
    updateSinceUntilBuild.set(false)

    pluginName.set("DiscordBeam")
    plugins.set(listOf("github", project(":HasteIt")))
}

tasks {
    patchPluginXml {
        changeNotes.set(provider {
            """
            1.20.0:
            * Switch to semantic versioning
            * Support for 2021.2
            """.trimIndent().run { markdownToHTML(this) }
        })
    }

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}