import org.jetbrains.changelog.markdownToHTML

version = "1.20.9"
group = "de.lukweb.share"

plugins {
    java
    idea
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
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
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    intellijPlatform {
        intellijIdeaCommunity(findProperty("idea.version").toString())

        pluginVerifier()
        zipSigner()
        instrumentationTools()

        pluginModule(implementation(project(":ShareBase")))
        localPlugin(project(":HasteIt"))
        bundledPlugin("org.jetbrains.plugins.github")
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "DiscordBeam"

        ideaVersion {
            untilBuild = provider { null }
        }
    }
}

tasks {
    patchPluginXml {
        changeNotes.set(provider {
            """
            1.20.9:    
            * Prepares for 2024.2 release
            
            1.20.8:
            * Prepares for 2024.1 release
                
            1.20.7:
            * Prepares for 2023.2 release

            1.20.6:
            * Prepares for 2023.1 release
                
            1.20.5:
            * Prepares for 2022.3 release
                
            1.20.4:
            * Prepares for 2022.2 release
            
            1.20.3
            * Builds the plugin using the latest version of the Gradle plugin
            
            1.20.2:
            * Upgrades library okhttp to version 4.9.3
                
            1.20.1:
            * Dispose settings view once the IDE shuts down
            
            1.20.0:
            * Switch to semantic versioning
            * Support for 2021.2
            """.trimIndent().run { markdownToHTML(this) }
        })
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}