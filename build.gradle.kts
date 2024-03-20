import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    kotlin("jvm") version "2.0.0-Beta4"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.sql"
version = "0.1-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    // sourceCompatibility = JavaVersion.VERSION_17
    // targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
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

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT") // paper api
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT") // acf
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.13") // if

    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.78.0")

}

tasks.processResources {
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
        "${rootProject.name}-${version}-all.jar", // file name
        "/mt", "/IS", "/IT", "/IM") // override, mt
}

tasks.shadowJar {
    relocate("co.aikar.commands", "me.sql.infiniteitems.acf")
    relocate("co.aikar.locales", "me.sql.infiniteitems.locales")

    relocate("com.github.stefvanschie.inventoryframework", "me.sql.infiniteitems.inventoryframework")

    minimize()
    finalizedBy("copyIntoPlugins")

}

tasks.compileKotlin {
    kotlinOptions.javaParameters = true
}

kotlin {
    jvmToolchain(17)
}