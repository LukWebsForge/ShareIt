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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.2")
    intellijPlatform {
        intellijIdeaCommunity(findProperty("idea.version").toString())

        pluginVerifier()
        zipSigner()
        instrumentationTools()

        pluginModule(implementation(project(":ShareBase")))
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "HasteIt"

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
            * Implements a new setting to provide an API key for hastebin
            
            1.20.6:
            * Prepares for 2023.1 release
                
            1.20.5:
            * Prepares for 2022.3 release
            * Switches back to the hastebin.com domain
                
            1.20.4:
            * Prepares for 2022.2 release 
                
            1.20.3:
            * Removes use of the deprecated message ballon API
                
            1.20.2:
            * Switches the default hastebin URL to [toptal.com] because hastebin has been acquired by them
            
            [toptal.com]: https://www.toptal.com/developers/hastebin/
                
            1.20.1:
            * Dispose settings view once the IDE shuts down
            
            1.20.0: 
            * Switch to semantic versioning
            * Support for 2021.2
            """.trimIndent().run { markdownToHTML(this) }
        })
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