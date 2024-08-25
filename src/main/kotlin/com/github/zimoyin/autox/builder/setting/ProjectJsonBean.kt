package com.github.zimoyin.autox.builder.setting

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.github.zimoyin.autox.builder.tools.JsonObject
import com.github.zimoyin.autox.builder.tools.JsonUtils
import com.github.zimoyin.autox.builder.tools.toJsonObject
import org.intellij.lang.annotations.Language
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
data class ProjectJsonBean(
    @JsonProperty("abis")
    val abis: ArrayList<String> = arrayListOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"),

    @JsonProperty("assets")
    val assets: ArrayList<JsonNode> = arrayListOf(),

    @JsonProperty("buildDir")
    val buildDir: String = "build",

    @JsonProperty("build")
    val build: JsonNode = JsonObject(),

    @JsonProperty("useFeatures")
    val useFeatures: ArrayList<String> = arrayListOf(),

    @JsonProperty("icon")
    val icon: String? = null,

    @JsonProperty("ignoredDirs")
    val ignoredDirs: ArrayList<String> = arrayListOf(),

    @JsonProperty("launchConfig")
    var launchConfig: RunSetting = RunSetting(),

    @JsonProperty("libs")
    var libs: LibSetting = LibSetting(),

    @JsonProperty("main")
    val main: String = "main.js",

    @JsonProperty("name")
    val name: String = "Test",

    @JsonProperty("outputPath")
    val outputPath: String = "./",

    @JsonProperty("packageName")
    val packageName: String = "com.zimoyin.autojs.wifi.example",

    @JsonProperty("projectDirectory")
    val projectDirectory: String = "./",

    @JsonProperty("scripts")
    val scripts: JsonNode = JsonObject(),

    @JsonProperty("signingConfig")
    val signingConfig: JsonNode = JsonObject(),

    @JsonProperty("sourcePath")
    val sourcePath: String = "./",

    @JsonProperty("versionCode")
    val versionCode: Int = 1,

    @JsonProperty("versionName")
    val versionName: String = "1.0.0"
) {
    @JsonIgnore
    var path: String? = null

    companion object {
        @JsonIgnore
        fun findFile(path: String): ProjectJsonBean {
            return covertTo(File(path).readText()).apply {
                this.path = path
            }
        }

        @JsonIgnore
        fun covertTo(@Language("JSON") json: String): ProjectJsonBean {
            return JsonUtils.jsonNodeToObject<ProjectJsonBean>(json.toJsonObject()).apply {
                if (libs.isEmpty()) {
                    libs.add(LibItem.LIBJACKPAL_ANDROIDTERM5_SO)
                    libs.add(LibItem.LIBJACKPAL_TERMEXEC2_SO)
                }
            }
        }
    }

    @JsonIgnore
    fun save(path: String) {
        File(path).writeText(JsonUtils.objectToJsonString(this))
    }
}