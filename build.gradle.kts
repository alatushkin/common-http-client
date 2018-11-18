import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.bundling.Jar
import java.util.Date

plugins {
    id("com.jfrog.bintray") version "1.8.1"
    `maven-publish`
    kotlin("jvm") version "1.3.0"
    java
}

group = "name.alatushkin.utils"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/alatushkin/maven")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    implementation("org.slf4j:slf4j-api:1.7.25")
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

publishing {

    repositories {
        maven {
            // change to point to your repo, e.g. http://my.org/repo
            url = uri("$buildDir/repo")
        }
    }
    publications {
        create("mavenJava", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar)
            artifactId = "common-http-client"

        }
    }
}

bintray {
    user = project.properties["bintrayUser"]?.toString() ?: System.getenv("BINTRAY_USER")
    key = project.properties["bintrayKey"]?.toString() ?: System.getenv("BINTRAY_KEY")
    setPublications("mavenJava")
    publish = true
    override = true

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "common-http-client"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/alatushkin/common-http-client.git"

        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = "${project.version}-snapshot"
            desc = "Common http client api"
            released = Date().toString()
            vcsTag = "${project.version}"
        })
    })
}