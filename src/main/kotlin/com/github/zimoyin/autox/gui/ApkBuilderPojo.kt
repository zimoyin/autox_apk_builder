package com.github.zimoyin.autox.gui

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.zimoyin.autox.builder.log
import java.io.File
import java.nio.file.Files


/**
 *
 * @author : zimo
 * @date : 2024/08/28
 */
data class ApkBuilderPojo(
    /**
     * 是否使用 GUI
     * 如果不使用则直接进行打包，启用后可以详细的设置打包
     */
    val gui: Boolean = true,

    /**
     * 综合资源后的路径，代表 GRADLE 编译后的，代表 AUTOJS 混合后的项目
     */
    val assets: List<String> = emptyList(),

    /**
     * 项目的 project.json，如果没有则让工具生成
     */
    val projectJson: String? = null,

    /**
     * 工作目录
     */
    var workDir: String? = null,

    /**
     * 图标路径
     */
    val iconPath: String? = null,

    /**
     * 开屏图片
     */
    val startIconPath: String? = null,

    /**
     * 模板 apk 路径
     */
    val templateApkPath: String? = null,

    /**
     * 签名文件
     */
    val signatureFile: String? = null,

    /**
     * 签名别名
     */
    val signatureAlias: String? = null,
    /**
     * 签名密码
     */
    val signaturePassword: String? = null,
) {
    @JsonIgnore
    var configPath = "./apk_builder_config.json"

    init {
        if (workDir == null) workDir = Files.createTempDirectory("autox_apk_builder").toFile().apply {
            deleteOnExit()
        }.absolutePath
        deleteCache()
    }

    fun deleteCache() {
        File(workDir, "template").delete()
        File(workDir, "decode").delete()
        File(workDir, "centralizedAssets").delete()
    }

    fun centralizedAssetsFile() = File(workDir!!, "centralizedAssets").apply { if (!exists()) mkdirs() }

    fun centralizedAssets(): String {
        val file = centralizedAssetsFile()
        if (assets.size == 1) {
            val assetFile = File(assets.first())
            if (assetFile.absolutePath == file.absolutePath) {
                // asset 数组就一个参数并且还是与 centralizedAssets 路径一致
                throw IllegalArgumentException("asset path is same as centralizedAssets path")
            }
        }
        log("[INFO] delete centralizedAssets cache: ${file.deleteRecursively()}")
        if (!file.exists()) file.mkdirs()
        for (asset in assets) {
            kotlin.runCatching {
                val assetFile = File(asset)
                if (assetFile.absolutePath == file.absolutePath) {
                    log("[WARN] asset path is same as centralizedAssets path")
                    return@runCatching
                }
                com.github.zimoyin.autox.builder.copy(assetFile, file)
            }.onFailure {
                log("[ERROR] ${it.message}")
            }
        }

        return file.absolutePath
    }
}
