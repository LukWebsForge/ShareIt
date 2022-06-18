import org.jetbrains.changelog.markdownToHTML

version = "1.20.4"
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
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