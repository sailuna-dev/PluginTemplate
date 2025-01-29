plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

val mcversion: String by project

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()
description = providers.gradleProperty("description").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.paper.api)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        archiveClassifier = ""
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(mcversion)
    }

    register<SetupTask>("setup")
}