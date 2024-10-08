package com.github.zimoyin.autox.gui.ui.setting

import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.builder.setting.LibItem
import com.github.zimoyin.autox.builder.setting.LibItem.*
import com.github.zimoyin.autox.builder.setting.LibSetting
import com.github.zimoyin.autox.builder.setting.PermissionsSetting.*
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import com.github.zimoyin.autox.builder.tools.JsonUtils
import com.github.zimoyin.autox.builder.tools.toJsonObject
import com.github.zimoyin.autox.gui.ApkBuilderPojo
import java.io.File
import java.util.concurrent.TimeoutException
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 *
 * @author : zimo
 * @date : 2024/08/28
 */
class SettingComponent {

    val libs = LibSetting()

    fun updateProjectJson() {
        if (projectJsonField.text == null || projectJsonField.text.isEmpty() || assetsField.text.isBlank()) return
        val projectJson = File(projectJsonField.text)
        if (!projectJson.exists()) return
        val projectJsonBean = ProjectJsonBean.findFile(projectJson.absolutePath)

        appNameField.text = projectJsonBean.name
        packageNameField.text = projectJsonBean.packageName
        versionNameField.text = projectJsonBean.versionName
        versionCodeField.text = projectJsonBean.versionCode.toString()
        if (appIconTextField.text.isBlank()) appIconTextField.text = projectJsonBean.icon?.let {
            if (File(it).exists()) it else null
        } ?: ""

        // 默认选中
        libTerminalCheckBox.isSelected = true
        libJackpalAndroidTerm5SoCheckBox.isSelected = true
        libJackpalTermexec2SoCheckBox.isSelected = true

        for (item in projectJsonBean.libs) {
            when (item) {
                LIBJACKPAL_ANDROIDTERM5_SO -> libTerminalCheckBox.isSelected = true
                LIBJACKPAL_TERMEXEC2_SO -> libJackpalTermexec2SoCheckBox.isSelected = true
                LIBOPENCV_JAVA4_SO -> libOpencvCheckBox.isSelected = true
                LIBCXX_SHARED_SO -> libcxxSharedSoCheckBox.isSelected = true
                LIBPADDLE_LIGHT_API_SHARED_SO -> libPaddleOcrCheckBox.isSelected = true
                LIBHIAI_SO -> libHiaiSoCheckBox.isSelected = true
                LIBHIAI_IR_SO -> libHiaiIrSoCheckBox.isSelected = true
                LIBHIAI_IR_BUILD_SO -> libHiaiIrBuildSoCheckBox.isSelected = true
                LIBNATIVE_SO -> libNativeSoCheckBox.isSelected = true
                LIBMLKIT_GOOGLE_OCR_PIPELINE_SO -> libGoogleORCCheckBox.isSelected = true
                LIBTESSERACT_SO -> libTesseractCheckBox.isSelected = true
                LIBPNG_SO -> libPngSoCheckBox.isSelected = true
                LIBLEPTONICA_SO -> libleptonicaSoCheckBox.isSelected = true
                LIBJPEG_SO -> libJpegSoCheckBox.isSelected = true
                LIBP7ZIP_SO -> lib7zipCheckBox.isSelected = true
            }
        }

        val runSetting = projectJsonBean.launchConfig
        displaySplashCheckBox.isSelected = runSetting.displaySplash
        hideDesktopIconCheckBox.isSelected = runSetting.hideLauncher
        stableModeCheckBox.isSelected = runSetting.stableMode
        hideLogCheckBox.isSelected = runSetting.hideLogs
        volumeKeyEndTaskCheckBox.isSelected = runSetting.volumeUpcontrol
        accessibilityDescriptionTextField.text = runSetting.serviceDesc
        startTextTextField.text = runSetting.splashText
        if (splashImageTextField.text.isBlank()) splashImageTextField.text = runSetting.splashIcon?.let {
            if (File(it).exists()) it else null
        } ?: ""

        for (permission in runSetting.permissions) {
            when (permission) {
                ACCESSIBILITY_SERVICES -> requestAccessibilityCheckBox.isSelected = true
                BACKGROUND_START -> requestBackgroundPopupPermissionCheckBox.isSelected = true
                DRAW_OVERLAY -> requestOverlayPermissionCheckBox.isSelected = true
            }
        }

        removeAccessibilityCheckBox.isSelected = runSetting.hideAccessibilityServices
    }


    private fun getCentralizedAssets(config: ApkBuilderPojo?): String? {
        return config?.assets?.let {
            if (it.size == 1) it.first() else config.centralizedAssets()
        }
    }

    fun updateConfig() {
        if (configField.text == null || configField.text.isEmpty() || configField.text.isBlank()) return
        val configPath = File(configField.text)
        if (!configPath.exists()) return
        val config = kotlin.runCatching { JsonUtils.stringToObject<ApkBuilderPojo>(configPath.readText()) }.getOrNull()
        workDirField.text = config?.workDir ?: ""
        assetsField.text = getCentralizedAssets(config) ?: ""
        projectJsonField.text = (config?.projectJson ?: "").let {
            if (File(it).exists()) it else ""
        }
        templateApkPathField.text = (config?.templateApkPath ?: "").let {
            if (File(it).exists()) it else ""
        }
        splashImageTextField.text = (config?.startIconPath ?: "").let {
            if (File(it).exists()) it else ""
        }
        appIconTextField.text = (config?.iconPath ?: "").let {
            if (File(it).exists()) it else ""
        }
        signatureFileTextField.text = (config?.signatureFile ?: "").let {
            if (File(it).exists()) it else ""
        }
        signatureAliasField.text = config?.signatureAlias ?: ""
        signaturePasswordField.text = config?.signaturePassword ?: ""
    }

    val configField = JTextField(20).apply {
        document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updateConfig()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                updateConfig()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                updateConfig()
            }
        })
    }
    val workDirField = JTextField(20)
    val projectJsonField = JTextField(20).apply {
        document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updateProjectJson()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                updateProjectJson()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                updateProjectJson()
            }
        })
    }
    val projectJsonButton = JButton("选择文件")
    val assetsField = JTextField(20).apply {
        fun update() {
            if (text == null || text.isEmpty() || text.isBlank()) {
                return
            }
            val file = File(text)
            if (!file.exists()) {
                return
            }
            Thread {
                val time = System.currentTimeMillis()
                // 在文件夹下递归查询，project.json
                val file2 = kotlin.runCatching {
                    file.walkTopDown().filter {
                        if (System.currentTimeMillis() - time > 1000) {
                            throw TimeoutException("Scanning projectjson file timed out")
                        }
                        it.isFile && it.name == "project.json"
                    }.toList().firstOrNull()
                }.onFailure {
                    log("[ERROR] ${it.message}")
                }.getOrNull()
                if (file2 != null) {
                    val ptext = projectJsonField.text
                    if (ptext != null && ptext.isNotEmpty() && ptext.isNotBlank() && File(ptext).exists()) {
                        return@Thread
                    }
                    projectJsonField.text = file2.absolutePath
                }
            }.start()
        }
        addActionListener {
            update()
        }
        document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                update()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                update()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                update()
            }
        })
    }
    val templateApkPathField = JTextField(20).apply {
        toolTipText = "如果没有则留空"

    }
    val templateApkPathButton = JButton("选择模板")


    val appNameField = JTextField(20)
    val packageNameField = JTextField(20)
    val versionNameField = JTextField(20)
    val versionCodeField = JTextField(20)


    val libTerminalCheckBox = JCheckBox("Terminal").apply {
        isSelected = true
        addItemListener {
            libJackpalAndroidTerm5SoCheckBox.isSelected = isSelected
            libJackpalTermexec2SoCheckBox.isSelected = isSelected
            if (isSelected) {
                libs.add(LibItem.LIBJACKPAL_ANDROIDTERM5_SO)
                libs.add(LibItem.LIBJACKPAL_TERMEXEC2_SO)
            } else {
                libs.remove(LibItem.LIBJACKPAL_ANDROIDTERM5_SO)
                libs.remove(LibItem.LIBJACKPAL_TERMEXEC2_SO)
            }
        }
    }

    val libJackpalAndroidTerm5SoCheckBox = JCheckBox("LIBJACKPAL_ANDROIDTERM5_SO").apply {
        isSelected = true
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBJACKPAL_ANDROIDTERM5_SO) else libs.remove(LibItem.LIBJACKPAL_ANDROIDTERM5_SO)
        }
    }
    val libJackpalTermexec2SoCheckBox = JCheckBox("LIBJACKPAL_TERMEXEC2_SO").apply {
        isSelected = true
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBJACKPAL_TERMEXEC2_SO) else libs.remove(LibItem.LIBJACKPAL_TERMEXEC2_SO)
        }
    }

    val libOpencvCheckBox = JCheckBox("Open CV").apply {
        addItemListener {
            libOpencvJava4SoCheckBox.isSelected = isSelected
            if (isSelected) libs.add(LibItem.LIBJACKPAL_TERMEXEC2_SO) else libs.remove(LibItem.LIBJACKPAL_TERMEXEC2_SO)
        }
    }
    val libOpencvJava4SoCheckBox = JCheckBox("LIBOPENCV_JAVA4_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBJACKPAL_TERMEXEC2_SO) else libs.remove(LibItem.LIBJACKPAL_TERMEXEC2_SO)
        }
    }

    val libPaddleOcrCheckBox = JCheckBox("Paddle OCR").apply {
        addItemListener {
            libcxxSharedSoCheckBox.isSelected = isSelected
            libPaddleLightApiSharedSoCheckBox.isSelected = isSelected
            libHiaiSoCheckBox.isSelected = isSelected
            libHiaiIrSoCheckBox.isSelected = isSelected
            libHiaiIrBuildSoCheckBox.isSelected = isSelected
            libNativeSoCheckBox.isSelected = isSelected
            if (isSelected) {
                libs.add(LibItem.LIBCXX_SHARED_SO)
                libs.add(LibItem.LIBPADDLE_LIGHT_API_SHARED_SO)
                libs.add(LibItem.LIBHIAI_SO)
                libs.add(LibItem.LIBHIAI_IR_SO)
                libs.add(LibItem.LIBHIAI_IR_BUILD_SO)
                libs.add(LibItem.LIBNATIVE_SO)
            } else {
                libs.remove(LibItem.LIBCXX_SHARED_SO)
                libs.remove(LibItem.LIBPADDLE_LIGHT_API_SHARED_SO)
                libs.remove(LibItem.LIBHIAI_SO)
                libs.remove(LibItem.LIBHIAI_IR_SO)
                libs.remove(LibItem.LIBHIAI_IR_BUILD_SO)
                libs.remove(LibItem.LIBNATIVE_SO)
            }
        }
    }
    val libcxxSharedSoCheckBox = JCheckBox("LIBCXX_SHARED_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBCXX_SHARED_SO) else libs.remove(LibItem.LIBCXX_SHARED_SO)
        }
    }
    val libPaddleLightApiSharedSoCheckBox = JCheckBox("LIBPADDLE_LIGHT_API_SHARED_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBPADDLE_LIGHT_API_SHARED_SO) else libs.remove(LibItem.LIBPADDLE_LIGHT_API_SHARED_SO)
        }
    }
    val libHiaiSoCheckBox = JCheckBox("LIBHIAI_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBHIAI_SO) else libs.remove(LibItem.LIBHIAI_SO)
        }
    }
    val libHiaiIrSoCheckBox = JCheckBox("LIBHIAI_IR_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBHIAI_IR_SO) else libs.remove(LibItem.LIBHIAI_IR_SO)
        }
    }
    val libHiaiIrBuildSoCheckBox = JCheckBox("LIBHIAI_IR_BUILD_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBHIAI_IR_BUILD_SO) else libs.remove(LibItem.LIBHIAI_IR_BUILD_SO)
        }
    }
    val libNativeSoCheckBox = JCheckBox("LIBNATIVE_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBNATIVE_SO) else libs.remove(LibItem.LIBNATIVE_SO)
        }
    }
    val libGoogleORCCheckBox = JCheckBox("Google ML KIT OCR").apply {
        addItemListener {
            libMlkitGoogleOcrPipelineSoCheckBox.isSelected = isSelected
            if (isSelected) libs.add(LibItem.LIBMLKIT_GOOGLE_OCR_PIPELINE_SO) else libs.remove(LibItem.LIBMLKIT_GOOGLE_OCR_PIPELINE_SO)
        }
    }
    val libMlkitGoogleOcrPipelineSoCheckBox = JCheckBox("LIBMLKIT_GOOGLE_OCR_PIPELINE_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBMLKIT_GOOGLE_OCR_PIPELINE_SO) else libs.remove(LibItem.LIBMLKIT_GOOGLE_OCR_PIPELINE_SO)
        }
    }
    val libTesseractCheckBox = JCheckBox("Tesseract OCR").apply {
        addItemListener {
            libTesseractSoCheckBox.isSelected = isSelected
            libPngSoCheckBox.isSelected = isSelected
            libleptonicaSoCheckBox.isSelected = isSelected
            libJpegSoCheckBox.isSelected = isSelected
            if (isSelected) {
                libs.add(LibItem.LIBTESSERACT_SO)
                libs.add(LibItem.LIBPNG_SO)
                libs.add(LibItem.LIBLEPTONICA_SO)
                libs.add(LibItem.LIBJPEG_SO)
            } else {
                libs.remove(LibItem.LIBTESSERACT_SO)
                libs.remove(LibItem.LIBPNG_SO)
                libs.remove(LibItem.LIBLEPTONICA_SO)
                libs.remove(LibItem.LIBJPEG_SO)
            }
        }
    }
    val libTesseractSoCheckBox = JCheckBox("LIBTESSERACT_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBTESSERACT_SO) else libs.remove(LibItem.LIBTESSERACT_SO)
        }
    }
    val libPngSoCheckBox = JCheckBox("LIBPNG_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBPNG_SO) else libs.remove(LibItem.LIBPNG_SO)
        }
    }
    val libleptonicaSoCheckBox = JCheckBox("LIBLEPTONICA_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBLEPTONICA_SO) else libs.remove(LibItem.LIBLEPTONICA_SO)
        }
    }
    val libJpegSoCheckBox = JCheckBox("LIBJPEG_SO").apply {
        addItemListener {
            if (isSelected) libs.add(LibItem.LIBJPEG_SO) else libs.remove(LibItem.LIBJPEG_SO)
        }
    }
    val lib7zipCheckBox = JCheckBox("7zip").apply {
        addItemListener {
            libP7zipSoCheckBox.isSelected = isSelected
            if (isSelected) libs.add(LibItem.LIBP7ZIP_SO) else libs.remove(LibItem.LIBP7ZIP_SO)
        }
    }
    val libP7zipSoCheckBox = JCheckBox("LIBP7ZIP_SO")


    val hideDesktopIconCheckBox = JCheckBox("隐藏桌面图标")
    val stableModeCheckBox = JCheckBox("稳定模式")
    val hideLogCheckBox = JCheckBox("隐藏日志")
    val displaySplashCheckBox = JCheckBox("显示启动界面")
    val volumeKeyEndTaskCheckBox = JCheckBox("音量上键结束任务")

    val requestAccessibilityCheckBox = JCheckBox("申请无障碍模式")
    val removeAccessibilityCheckBox = JCheckBox("移除无障碍模式")
    val requestOverlayPermissionCheckBox = JCheckBox("需要悬浮窗权限")
    val requestBackgroundPopupPermissionCheckBox = JCheckBox("需要后台弹出界面的权限")

    //无障碍描述
    val accessibilityDescriptionTextField = JTextField(20).apply {
        text = "Autox WIFI"
    }

    // 开屏文本
    val startTextTextField = JTextField(20).apply {
        text = "Powered by Autoxjs.com. Packaged by IDEA plugin Autojsx WIFI provided"
    }

    // 开屏图片
    val splashImageTextField = JTextField(20)
    val splashImageButton = JButton("选择图片")

    val appIconTextField = JTextField(20)
    val appIconButton = JButton("选择图标")

    val signatureFileTextField = JTextField(20)
    val signatureFileButton = JButton("选择文件")

    val signatureAliasField = JTextField(20)

    //    val signaturePasswordField = JPasswordField(20)
    val signaturePasswordField = JTextField(20)
}
