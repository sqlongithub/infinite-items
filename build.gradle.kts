import org.apache.logging.log4j.util.Chars
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.ir.backend.js.compile
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.*

plugins {
    java
    kotlin("jvm") version "2.0.0-Beta5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "me.sql"
version = "0.1-SNAPSHOT"

java {

    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    // sourceCompatibility = JavaVersion.VERSION_17
    // targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven {
        url = uri("https://api.modrinth.com/maven")
    }
    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
    maven {
        name = "Spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

abstract class FilterTransform : TransformAction<FilterTransform.Parameters> {
    interface Parameters : TransformParameters {
        @get:Input
        val files: ListProperty<String>
    }

    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val input = inputArtifact.get().asFile.toURI()
        FileSystems.newFileSystem(URI.create("jar:$input"), Collections.emptyMap<String, String>()).use { zip ->
            parameters.files.get().forEach {
                val path = zip.getPath(it)
                if (Files.exists(path)) {
                    Files.delete(path)
                    println("Deleted $path from artifact.")
                }
            }
        }
    }
}

val artifactType = Attribute.of("artifactType", String::class.java)
val filtered = Attribute.of("filtered", Boolean::class.javaObjectType)

dependencies {
    attributesSchema {
        attribute(filtered)
    }
    artifactTypes.getByName("jar") {
        attributes.attribute(filtered, false)
    }

    testImplementation(kotlin("test"))

    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT") // acf
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.13") // if

    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.78.0") //{
//        attributes {
//            attributes.attribute(filtered, true)
//        }
//    }

//    registerTransform(FilterTransform::class) {
//        from.attribute(filtered, false).attribute(artifactType, "jar")
//        to.attribute(filtered, true).attribute(artifactType, "jar")
//        parameters {
//            files.set(listOf("/META-INF/services/net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer\$Provider"))
//        }
//    }
}

//configurations.testImplementation {
//    exclude("io.papermc.paper", "paper-server")
//}

tasks.processResources {
    charset(Charsets.UTF_8.name())
    filesMatching("**/plugin.yml") {
        expand(project.properties)
    }
}

tasks.withType<Test> {
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        debug {
            events(
                TestLogEvent.STARTED,
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat

        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                val output = "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n" + ("-".repeat(repeatLength)) + "\n" + startItem + output + endItem + "\n" + ("-".repeat(repeatLength)))
            }
        }))
    }
    useJUnitPlatform()
}

task<Exec>("copyIntoPlugins") {
    setIgnoreExitValue(true)
    commandLine("ROBOCOPY",
        "${projectDir}/build/libs/", // from dir
        "C:\\Users\\Niclas\\Documents\\test_server\\plugins", // to dir
        "${rootProject.name}-${version}.jar", // file name
        "/mt", "/IS", "/IT", "/IM") // override, mt
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
    finalizedBy("copyIntoPlugins")
}

tasks.shadowJar {
    relocate("co.aikar.commands", "me.sql.infiniteitems.acf")
    relocate("co.aikar.locales", "me.sql.infiniteitems.locales")

    relocate("com.github.stefvanschie.inventoryframework", "me.sql.infiniteitems.inventoryframework")

    // archiveFileName = "${rootProject.name}-${version}.jar"

    minimize()
}

tasks.compileJava {
    charset(Charsets.UTF_8.name())
}

tasks.javadoc {
    charset(Charsets.UTF_8.name())
}

tasks.compileKotlin {
    charset(Charsets.UTF_8.name()) // We want UTF-8 for everything
    kotlinOptions.javaParameters = true
}

kotlin {
    jvmToolchain(17)
}