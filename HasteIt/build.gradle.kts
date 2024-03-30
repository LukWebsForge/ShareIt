import org.jetbrains.changelog.markdownToHTML

version = "1.20.8"
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation(project(":ShareBase"))
}

intellij {
    version.set(findProperty("idea.version").toString())
    updateSinceUntilBuild.set(false)

    pluginName.set("HasteIt")
}

tasks {
    patchPluginXml {
        changeNotes.set(provider {
            """
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