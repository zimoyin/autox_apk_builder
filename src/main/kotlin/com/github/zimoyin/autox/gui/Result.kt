package com.github.zimoyin.autox.gui

import com.github.zimoyin.autox.builder.AutoxApkBuilder
import com.github.zimoyin.autox.builder.SignConfig
import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import java.io.File
import java.io.IOException

/**
 *
 * @author : zimo
 * @date : 2024/08/28
 */
data class Result(
    val config: ApkBuilderPojo,
    val project: ProjectJsonBean
) {
    var onSuccess: ((File) -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null

    fun build(): File? {
         config.workDir?.let {workDir->
             val workFile = File(workDir).absoluteFile
             for (asset in config.assets) {
                if (File(asset).absoluteFile == workFile || workFile.absolutePath.contains(File(asset).absolutePath)){
                    // assets 不能是 workFile 的兄弟或者父亲文件夹
                    val t = IllegalStateException("Assets cannot be the sibling or parent folder of workFile")
                    if (onError != null) {
                        onError?.invoke(t)
                    } else {
                        log("[ERROR] Assets cannot be the sibling or parent folder of workFile")
                        t.printStackTrace()
                    }
                    return null
                }
            }
        }



        return kotlin.runCatching { build0() }.onFailure {
            if (onError != null) {
                onError?.invoke(it)
            } else {
                log("[ERROR] Build APK Failed\n${it.message}")
                it.printStackTrace()
            }
        }.onSuccess {
            log("Build APK Success: $it")
            if (onSuccess != null) {
                onSuccess?.invoke(it)
            }
            config.deleteCache()
        }.getOrNull()
    }

    private fun build0(): File {
        config.deleteCache()
        config.centralizedAssets()
        val workDir = config.workDir.let {
            if (it == null) throw IllegalArgumentException("workDir path is null")
            val file = File(it)
            file.mkdirs()
            if (file.exists().not() || file.isFile || file.isDirectory.not()) {
                throw IllegalArgumentException("workDir path is wrong: \n$it")
            }
            file
        }

        val asset = config.centralizedAssetsFile().let { file ->
            if (file == null || file.exists().not() || file.isFile || file.isDirectory.not()) {
                throw IllegalArgumentException("asset path is wrong: \n$file")
            }
            file
        }
        val iconPath = config.iconPath
        val splashPath = config.startIconPath
        // 构建
        return AutoxApkBuilder()
            .setWorkDir(workDir)
            .setProjectJson(project)
            .setAssets(asset.absolutePath)
            .apply {
                if (!iconPath.isNullOrEmpty() && iconPath.isNotBlank()) setIconPath(iconPath)
                if (!splashPath.isNullOrEmpty() && splashPath.isNotBlank()) setStartIconPath(splashPath)
                kotlin.runCatching {
                    setSignConfig(buildSignConfig(config))
                }.onFailure {
                    log("[INFO] sign config is null")
                }
            }
            .build()
    }

    private fun buildSignConfig(config: ApkBuilderPojo) = SignConfig(
        keyStorePath = config.signatureFile ?: throw IllegalArgumentException("signatureFile is null"),
        keyAlias = config.signatureAlias ?: throw IllegalArgumentException("signatureAlias is null"),
        keyStorePassword = config.signaturePassword ?: throw IllegalArgumentException("signaturePassword is null"),
    )

    companion object {

        /**
         * 根据操作系统类型打开指定的文件夹路径
         * @param parentPath 文件夹路径
         */
        fun openFileExplorer(parentPath: String) {
            // 获取操作系统名称，并转为小写
            val os = System.getProperty("os.name").lowercase()
            val command: String

            // 判断操作系统类型，并设置相应的命令
            command = when {
                os.contains("win") -> "explorer"
                os.contains("mac") || os.contains("nix") || os.contains("nux") -> "open"
                else -> {
                    println("Unsupported OS: $os")
                    return
                }
            }

            // 拼接命令和文件路径
            val openCommand = "$command $parentPath"

            try {
                // 执行命令
                Runtime.getRuntime().exec(openCommand)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
}