package com.github.zimoyin.autox.gui

import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import com.github.zimoyin.autox.builder.tools.JsonObject
import com.github.zimoyin.autox.builder.tools.toJsonObject
import com.github.zimoyin.autox.builder.tools.writeToFile
import java.io.File
import java.util.concurrent.TimeoutException

const val GUI_VERSION = "1.0.4"

fun main(args: Array<String>) {
    for (arg in args) {
        if (arg.contains("createConfig")) {
            if (!File("./apk_builder_config.json").exists()) ApkBuilderPojo()
                .toJsonObject()
                .writeToFile("./apk_builder_config.json")
        }
        if (arg.contains("help")) {
            println("-createConfig: Create config file")
            println("-noGui: Disable GUI")
            println("-help: Show this help")
            println("apk_builder_config.json path")
            return
        }
    }
    log("Version: $GUI_VERSION")
    val isGUI = isGui(args)
    val config = getConfig(args)

    // 如果开启GUI（存在配置文件且gui 为 true  或者 不存在配置文件）
    if (config.isNotNull()) {
        if (isGUI) {
            Application.start(config)
        } else {
            runNoGui(config)
        }
    } else {
        if (isGUI) Application.start()
        else throw IllegalArgumentException("Not found config file. Use -createConfig to create config file")
    }
}

private fun runNoGui(config: ApkBuilderPojo?) {
    var projectJsonPath = config!!.projectJson
    if (config.assets.isEmpty()) throw IllegalArgumentException("assets is null")
    val assets = config.centralizedAssets()

    val thread = Thread {
        val time = System.currentTimeMillis()
        // 在文件夹下递归查询，project.json
        projectJsonPath = runCatching {
            File(assets).walkTopDown().filter {
                if (System.currentTimeMillis() - time > 1000) {
                    throw TimeoutException("Scanning projectjson file timed out")
                }
                it.isFile && it.name == "project.json"
            }.toList().firstOrNull()?.absolutePath
        }.onFailure {
            log("[ERROR] ${it.message}")
        }.getOrNull()
    }
    if (projectJsonPath == null) {
        thread.start()
        Thread.sleep(1000)
    }
    projectJsonPath = projectJsonPath ?: throw IllegalArgumentException("projectJson is null")
    if (File(projectJsonPath!!).exists().not()) throw IllegalArgumentException("projectJson is not found")
    val bean = ProjectJsonBean.findFile(projectJsonPath!!)
    Result(config, bean).build()
}

private fun Any?.isNotNull(): Boolean {
    return this != null
}

private fun getConfig(args0: Array<String>): ApkBuilderPojo? {
    val args = args0.toMutableList().apply { add("./apk_builder_config.json") }
    for (arg in args) {
        val file = File(arg)
        if (file.exists()) {
            runCatching {
                val json = JsonObject(file.readText())
                val gui = json.getBoolean("gui")
                val assets = json.getObject<List<String>>("assets")
                return  json.parseToObject<ApkBuilderPojo>()
            }
        }
    }
    return null
}

private fun isGui(args0: Array<String>): Boolean {
    val args = args0.toMutableList().apply { add("./apk_builder_config.json") }
    var result = true
    for (arg in args) {
        if (!result) return false
        if (arg.contains("noGui")) return false

        val file = File(arg)
        if (file.exists()) {
            runCatching {
                result = JsonObject(file.readText()).getBoolean("gui")
                if (result){
                    return true
                }
            }
        }
    }
    return result
}