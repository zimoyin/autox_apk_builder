plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "com.github"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(files("libs/apksigner/0.9/apksigner.jar"))
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    implementation("org.dom4j:dom4j:2.1.4")

    implementation("org.apktool:apktool-cli:2.9.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
//            // 添加 apksigner.jar
//            artifact(file("libs/apksigner/0.9/apksigner.jar")) {
//                classifier = "apksigner"
//            }

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}