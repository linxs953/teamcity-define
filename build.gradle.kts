plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://download.jetbrains.com/teamcity-repository")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    // TeamCity Kotlin DSL
    implementation("org.jetbrains.teamcity:teamcity-rest-client:2024.3")
    implementation("org.jetbrains.teamcity:teamcity-dsl:2024.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveFileName.set("${project.name}-${version}.jar")
    manifest {
        attributes("Main-Class" to "com.example.Main")
    }
}
