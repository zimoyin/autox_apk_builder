import org.jetbrains.kotlin.incremental.deleteDirectoryContents
import org.panteleyev.jpackage.ImageType
import org.panteleyev.jpackage.JPackageTask

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.panteleyev.jpackageplugin") version "1.6.0"
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
    doFirst {
        File(project.projectDir, "build/libs").apply {
            if (exists()) deleteDirectoryContents()
        }
    }
    manifest {
        attributes["Main-Class"] = "com.github.zimoyin.autox.gui.MainKt"
    }
}

// 让 jar 任务依赖于 shadowJar
tasks.jar {
    dependsOn(tasks.shadowJar)
    doFirst {
        File(project.projectDir, "build/libs").apply {
            if (exists()) deleteDirectoryContents()
        }
    }
    enabled = false
}

tasks.jpackage {
    // jpackage 环境变量 可以选择将环境变量传递给jpackage可执行进程。
    jpackageEnvironment = mapOf()
    // jpackage 链接选项
    jLinkOptions = listOf()
    jLinkOptions = listOf(
        "--no-header-files",
        "--no-man-pages",
        "--strip-debug",
        "--strip-native-commands"
    )

    // 程序启动追加参数
    arguments = listOf()
    // JVM 参数
    javaOptions = listOf("-Xmx300m")

    val build = File(project.projectDir, "build")
    // JPackage 参数
    share {
        appName = "Autox Apk Builder"
        appVersion = version.toString()
        input = File(build, "libs").absolutePath
        mainJar = findLatestJarInLibs(input)?.name
        destination = File(build, "jpackage").absolutePath
        addModules = null
    }
    windows {
        aboutUrl = "https://github.com/zimoyin/autox_apk_builder"
        winDirChooser = true
        winMenu = true
        winShortcut = true
        winShortcutPrompt = true
        installDir = rootProject.name
        winConsole = false
        icon = File(build, "resources/main/build.ico").absolutePath
        type = ImageType.EXE
    }
}

fun findLatestJarInLibs(libDirectory0: String): File? {
    val libDirectory = File(libDirectory0)
    return libDirectory.listFiles { file -> file.extension == "jar" }
        ?.maxByOrNull { it.lastModified() }
}

fun JPackageTask.share(call: JPackageTask.() -> Unit) {
    call()
}