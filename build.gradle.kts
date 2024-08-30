import groovy.xml.dom.DOMCategory.attributes

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.zimoyin"
version = "1.0.4"

repositories {
    google()
    mavenCentral()
}


dependencies {
    implementation(files("libs/apksigner/0.9/apksigner.jar"))
//    implementation(files("libs/apktool/2.9.3/apktool.jar"))

    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("org.dom4j:dom4j:2.1.4")
    implementation("org.apktool:apktool-cli:2.9.3")
    implementation("com.formdev:flatlaf:3.5.1")
//    runtimeOnly("com.formdev:flatlaf-intellij-themes:3.5.1")



    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// 使用 shadowJar 任务替代 jar 任务生成一个包含所有依赖的可执行 JAR 文件
tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "com.github.zimoyin.autox.gui.MainKt"
    }
}

// 让 jar 任务依赖于 shadowJar
tasks.jar {
    dependsOn(tasks.shadowJar)
    enabled = false
}