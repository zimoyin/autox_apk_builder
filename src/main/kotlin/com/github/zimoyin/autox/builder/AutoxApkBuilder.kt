package com.github.zimoyin.autox.builder

import com.github.zimoyin.autox.builder.setting.LibSetting
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import com.github.zimoyin.autox.builder.setting.RunSetting
import com.github.zimoyin.autox.builder.tools.*
import java.io.File
import java.nio.file.Files

const val TEMPLATE_PATH = "template/6.4.3/template.apk"

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
class AutoxApkBuilder {
    private val assets: MutableList<String> = mutableListOf()
    private val temp = Files.createTempDirectory("idea_plug_autox_wifi_build_apk").toFile()
    private var workDir: File = temp
    private var projectJson: ProjectJsonBean? = null
    private var templateApkPath: String? = null
    private var signConfig: SignConfig? = null
    private var startIconPath: String? = null
    private var iconPath: String? = null
    private var runSetting: RunSetting? = null
    private var libSetting: LibSetting? = null

    /**
     * 运行配置，对应 project.json 中的 launchConfig
     */
    fun setRunSetting(runSetting0: RunSetting): AutoxApkBuilder {
        runSetting = runSetting0
        return this
    }

    /**
     * 依赖配置，对应 project.json 中的 libs
     */
    fun setLibSetting(libSetting0: LibSetting): AutoxApkBuilder {
        libSetting = libSetting0
        return this
    }

    /**
     * 工作目录，默认工作目录为临时文件。该临时文件不会在 build() 方法执行后完全删除
     */
    fun setWorkDir(workDir0: File): AutoxApkBuilder {
        workDir = workDir0
        return this
    }

    /**
     * 应用图标，在此处指定之后会通过修改 xml 文件来替换图标。如果你知道 autox projec.json 的 icon 机制可以从那指定，但不一定有效
     */
    fun setIconPath(iconPath0: String): AutoxApkBuilder {
        if (iconPath0.isEmpty() || iconPath0.isBlank()) throw IllegalArgumentException("iconPath is empty.")
        iconPath = iconPath0
        return this
    }

    /**
     * 添加 js 源码到apk文件中。注意 project.json 不应该在二级文件夹下
     * @param assets 源码以及资源文件夹的位置，设定之后会读取文件并复制到 assets/project/。注意最外层的文件夹不会被移动
     */
    fun setAssets(vararg assets: String): AutoxApkBuilder {
        this.assets.addAll(assets.toList())
        if (projectJson == null) for (asset in assets) {
            if (projectJson != null) break
            val file = File(asset)
            if (file.isFile && file.name == "project.json") {
                projectJson = ProjectJsonBean.findFile(asset)
            }
            if (file.isDirectory) {
                findProjectJson(file.absolutePath)?.let {
                    projectJson = it
                }
            }
        }
        return this
    }

    private fun findProjectJson(path: String): ProjectJsonBean? {
        val file = File(path)
        if (file.isFile && file.name == "project.json") {
            return ProjectJsonBean.findFile(path)
        }
        if (file.isDirectory) {
            for (file0 in file.listFiles() ?: emptyArray()) {
                if (file0.isFile && file0.name == "project.json") {
                    return ProjectJsonBean.findFile(file0.absolutePath)
                }
            }
        }
        return null
    }

    /**
     * 启动页面图标，此处指定之后会覆盖源图标。如果你知道 autox projec.json 替换图标的机制可以从那指定
     * @param startIconPath0 启动页面图标（png）
     */
    fun setStartIconPath(startIconPath0: String): AutoxApkBuilder {
        if (startIconPath0.isEmpty() || startIconPath0.isBlank()) throw IllegalArgumentException("startIconPath is empty.")
        startIconPath = startIconPath0
        return this
    }

    /**
     * 项目配置，对应 project.json，如果不指定会从源码位置查找
     */
    fun setProjectJson(projectJson0: ProjectJsonBean): AutoxApkBuilder {
        projectJson = projectJson0
        return this
    }

    /**
     * 模板 apk 路径，如果不指定会从 resources/template/.../template.apk 中获取。并释放到工作路径
     */
    fun setTemplateApkPath(templateApkPath0: String): AutoxApkBuilder {
        templateApkPath = templateApkPath0
        return this
    }

    /**
     * 签名配置
     */
    fun setSignConfig(signConfig0: SignConfig): AutoxApkBuilder {
        val path = signConfig0.keyStorePath
        if (path.isEmpty() || path.isBlank()) throw IllegalArgumentException("keyStorePath is empty.")
        signConfig = signConfig0
        return this
    }

    fun build(): File {
        return try {
            build0()
        } catch (e: Exception) {
            temp.deleteRecursively()
            throw e
        }
    }

    private fun build0(): File {
        // 获取项目配置
        val projectJson0 =
            projectJson ?: throw IllegalArgumentException("projectJson not found. Please set it to projectJson.")
        if (libSetting != null) projectJson0.libs = libSetting!!
        if (runSetting != null) projectJson0.launchConfig = runSetting!!

        // 释放模板
        val templateApkPath0 = templateApkPath ?: TEMPLATE_PATH.let {
            val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(TEMPLATE_PATH)
                ?: throw NullPointerException("template apk not found. Please download template.apk from Github Release and place it in resources/template/6.4.3/template.apk OR set it to templateApkPath.")
            // 循环读取字节，将 apk 读取到工作目录
            val file = File(workDir, TEMPLATE_PATH)
            file.parentFile.mkdirs()
            file.createNewFile()
            stream.use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
            file.absolutePath
        }
        log("[Phased 1/8] template apk path: $templateApkPath0")

        // 解压APk
        val decodeDir = File(workDir, "decode")
        decodeDir.mkdirs()
        ApkTool.decodeApk(templateApkPath0, decodeDir.absolutePath)
        log("[Phased 2/8] decode apk end path: ${decodeDir.absolutePath}")

        // 复制 assets 到 assets/project/
        val assetDir = File(decodeDir, "assets/project")
        assetDir.mkdirs()
        for (asset in assets) {
            val assetFile = File(asset)
            assetFile.copyDest(assetDir)
        }
        log("[Phased 3/8] copy assets end path: ${assetDir.absolutePath}")

        // 反序列化 project.json 到 assets/project/
        val project = File(decodeDir, "assets/project/project.json")
        project.delete()
        JsonUtils.objectToJsonNode(projectJson0).writeToFile(project.absolutePath)
        log("[Phased 4/8] copy project.json end path: ${project.absolutePath}")

        // 处理 安卓清单文件
        val manifest = File(decodeDir, "AndroidManifest.xml")
        XmlTool(projectJson0).modify(manifest, iconPath).writeToFile(manifest.absolutePath)
        log("[Phased 5/8] modify AndroidManifest.xml end path: ${manifest.absolutePath}")

        // 处理启动页面图标
        if (startIconPath != null) {
            val drawable = File(decodeDir, "res/drawable/")
            drawable.mkdirs()
            val drawableMdpiV4 = File(decodeDir, "res/drawable-mdpi-v4/")
            drawableMdpiV4.mkdirs()
            val startIconFile = File(startIconPath!!)
            if (startIconFile.exists()) {
                if (startIconFile.extension.lowercase() != "png") {
                    throw IllegalArgumentException("start icon must be png: $startIconFile")
                }
                startIconFile.copyDest(drawable, "autojs_logo.png")
                startIconFile.copyDest(drawableMdpiV4, "autojs_logo.png")
                log("copy start icon end path: ${drawable.absolutePath}")
                log("copy start icon end path: ${drawableMdpiV4.absolutePath}")
            } else {
                log("[ERROR] start icon not found: $startIconPath")
            }
        }

        // 处理应用图标
        if (iconPath != null) {
            val drawable = File(decodeDir, "res/drawable/")
            drawable.mkdirs()
            val iconFile = File(iconPath!!)
            if (iconFile.exists()){
                iconFile.copyDest(drawable, "application")
                log("copy icon end path: ${drawable.absolutePath}")
            }else{
                log("[ERROR] icon not found: $iconPath")
            }
        }


        // 压缩APK
        val encodeApk = File(workDir, "${projectJson0.name}.apk")
        ApkTool.encodeApk(decodeDir.absolutePath, encodeApk.absolutePath)
        log("[Phased 6/8] encode apk end path: ${encodeApk.absolutePath}")

        // 签名APK，签名使用 workDir
        val signApk = File(workDir, "${projectJson0.name}.apk")
        if (signConfig == null) {
            SignTool.signApk(signApk.absolutePath)
        } else {
            SignTool.signApk(
                signApk.absolutePath,
                signConfig!!.keyStorePath,
                signConfig!!.keyAlias,
                signConfig!!.keyStorePassword,
            )
        }
        log("[Phased 7/8] sign apk end path: ${signApk.absolutePath}")

        // 清理缓存
        decodeDir.deleteRecursively()
        File(templateApkPath0).delete()
        temp.deleteOnExit()
        log("[Phased 8/8] clean temp end")
        log("!!!!!!!!!!!!!!!! THE END !!!!!!!!!!!!!!!!")
        if (signApk.exists()) log("SUCCESS")
        else log("FAIL")
        return signApk
    }
}

/**
 * 拷贝文件到目标文件夹
 * @param destination 目标文件夹
 * @param name 如果 this 是文件的话，则重命名
 */
fun File.copyDest(destination: File, name: String? = null) {
    com.github.zimoyin.autox.builder.copy(this, destination, name)
}

/**
 * 拷贝文件到目标文件夹
 * @param source 源文件夹/文件
 * @param destination 目标文件夹
 * @param name 如果 source 是文件的话，则重命名
 */
fun copy(source: File, destination: File, name: String? = null) {
    if (!source.exists()) {
        throw IllegalArgumentException("Source directory doesn't exist: ${source.absolutePath}")
    }

    if (!destination.exists()) {
        destination.mkdirs()
    }

    if (source.isFile || !source.isDirectory) {
        val destFile = File(destination, name ?: source.name)
        source.copyTo(destFile, overwrite = true)
        return
    }

    source.listFiles()?.forEach { file ->
        val destFile = File(destination, file.name)
        if (file.isDirectory) {
            copy(file, destFile)
        } else {
            file.copyTo(destFile, overwrite = true)
        }
    }
}

data class SignConfig(
    val keyStorePath: String,
    val keyStorePassword: String,
    val keyAlias: String,
//    val keyPassword: String
)