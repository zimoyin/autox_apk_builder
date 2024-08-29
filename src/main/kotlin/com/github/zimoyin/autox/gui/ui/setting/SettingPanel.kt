package com.github.zimoyin.autox.gui.ui.setting

import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.builder.setting.LibItem.*
import com.github.zimoyin.autox.builder.setting.PermissionsSetting.*
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import com.github.zimoyin.autox.gui.ApkBuilderPojo
import com.github.zimoyin.autox.gui.ui.IPanel
import com.github.zimoyin.autox.gui.ui.setting.handle.SettingDataHandle
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess

/**
 *
 * @author : zimo
 * @date : 2024/08/28
 */
class SettingPanel(private val config: ApkBuilderPojo? = null) : IPanel(GridBagLayout()) {

    private val scmt = SettingComponent().apply {
        init()
    }
    private var Y = 0
    private val c = GridBagConstraints().apply {
        fill = GridBagConstraints.HORIZONTAL
        insets = Insets(5, 5, 5, 5)
    }

    private fun SettingComponent.init() {
        workDirField.text = config?.workDir ?: ""
        assetsField.text = config?.centralizedAssets() ?: ""
        projectJsonField.text = (config?.projectJson ?: "").let {
            if (File(it).exists()) it else ""
        }
        templateApkPathField.text = config?.templateApkPath ?: ""
        splashImageTextField.text = config?.startIconPath ?: ""
        appIconTextField.text = config?.iconPath ?: ""
        signatureFileTextField.text = config?.signatureFile ?: ""
        signatureAliasField.text = config?.signatureAlias ?: ""
        signaturePasswordField.text = config?.signaturePassword ?: ""

        Thread{
            Thread.sleep(1100)
            if (projectJsonField.text == null || projectJsonField.text.isEmpty() || assetsField.text.isEmpty()) return@Thread
            val projectJson = File(projectJsonField.text)
            if (!projectJson.exists()) return@Thread
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
        }.start()
    }

    override fun createPanel(panel: IPanel): JComponent {
        addLabel("工作环境:")
        addFileChooserField(
            "project.json 路径:",
            scmt.projectJsonField,
            scmt.projectJsonButton,
            FileNameExtensionFilter("JSON File", "json")
        )
        addField("assets 路径:", scmt.assetsField)
        addField("工作路径:", scmt.workDirField)
        addFileChooserField(
            "APK模板路径:",
            scmt.templateApkPathField,
            scmt.templateApkPathButton,
            FileNameExtensionFilter("APK File", "apk")
        )

        addJSeparator()
        addLabel("包名信息:")
        addField("应用名称:", scmt.appNameField)
        addField("包名:", scmt.packageNameField)
        addField("版本名称:", scmt.versionNameField)
        addField("版本号 (纯数字):", scmt.versionCodeField)

        c.gridwidth = 2
        addJSeparator()
        addLabel("选择 Lib:")
        addLabel("Terminal")
        addCheckBox(scmt.libTerminalCheckBox)
        addCheckBox(scmt.libJackpalAndroidTerm5SoCheckBox)
        addCheckBox(scmt.libJackpalTermexec2SoCheckBox)
        addJSeparator(Color(0xA9A9A9), 1, 1)
        addLabel("Open CV")
        addCheckBox(scmt.libOpencvCheckBox)
        addCheckBox(scmt.libOpencvJava4SoCheckBox)
        addJSeparator(Color(0xA9A9A9), 1, 1)
        addLabel("Paddle OCR")
        addCheckBox(scmt.libPaddleOcrCheckBox)
        addCheckBox(scmt.libcxxSharedSoCheckBox)
        addCheckBox(scmt.libPaddleLightApiSharedSoCheckBox)
        addCheckBox(scmt.libHiaiSoCheckBox)
        addCheckBox(scmt.libHiaiIrSoCheckBox)
        addCheckBox(scmt.libHiaiIrBuildSoCheckBox)
        addCheckBox(scmt.libNativeSoCheckBox)
        addJSeparator(Color(0xA9A9A9), 1, 1)
        addLabel("Google ML Kit OCR")
        addCheckBox(scmt.libGoogleORCCheckBox)
        addCheckBox(scmt.libMlkitGoogleOcrPipelineSoCheckBox)
        addJSeparator(Color(0xA9A9A9), 1, 1)
        addLabel("TESSERACT OCR")
        addCheckBox(scmt.libTesseractCheckBox)
        addCheckBox(scmt.libTesseractSoCheckBox)
        addCheckBox(scmt.libleptonicaSoCheckBox)
        addCheckBox(scmt.libPngSoCheckBox)
        addCheckBox(scmt.libJpegSoCheckBox)
        addJSeparator(Color(0xA9A9A9), 1, 1)
        addLabel("7zip")
        addCheckBox(scmt.lib7zipCheckBox)
        addCheckBox(scmt.libP7zipSoCheckBox)

        addJSeparator()
        addLabel("应用设置:")
        addCheckBox(scmt.displaySplashCheckBox)
        addCheckBox(scmt.hideDesktopIconCheckBox)
        addCheckBox(scmt.stableModeCheckBox)
        addCheckBox(scmt.hideLogCheckBox)
        addCheckBox(scmt.volumeKeyEndTaskCheckBox)

        addJSeparator()
        addLabel("权限设置:")
        addCheckBox(scmt.requestAccessibilityCheckBox)
        addCheckBox(scmt.removeAccessibilityCheckBox)
        addCheckBox(scmt.requestOverlayPermissionCheckBox)
        addCheckBox(scmt.requestBackgroundPopupPermissionCheckBox)

        addField("启动界面文本:", scmt.startTextTextField)
        addField("无障碍描述:", scmt.accessibilityDescriptionTextField)

        addJSeparator()
        addLabel("图标设置:")
        addFileChooserField(
            "开屏图片:",
            scmt.splashImageTextField,
            scmt.splashImageButton,
            FileNameExtensionFilter("PNG Image File", "png")
        )
        addFileChooserField(
            "应用图标:",
            scmt.appIconTextField,
            scmt.appIconButton,
            FileNameExtensionFilter("Image File", "png", "jpg", "jpeg", "ico", "icon")
        )

        addJSeparator()
        addLabel("签名设置:")
        addFileChooserField(
            "签名文件:",
            scmt.signatureFileTextField,
            scmt.signatureFileButton,
            FileNameExtensionFilter("Key File", "jks", "keystore", ".p12", ".pkcs12")
        )
        addField("签名别名:", scmt.signatureAliasField)
        addField("签名密码:", scmt.signaturePasswordField)

        addOkButton()
        addClearButton()
        addExitButton()
        val scrollPane = JScrollPane(panel).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBar.unitIncrement = 25
            verticalScrollBar.blockIncrement = 50
        }

        return scrollPane
    }

    private fun addOkButton(y: Int = Y++) {
        add(JButton("确定").apply {
            // 监听
            background = Color(0x375A81)
            addActionListener {
                log("开始生成APK")
                SettingDataHandle(scmt).handle()
            }
        }, c.apply {
            c.gridwidth = 0
            gridy = y
            gridx = 0
            insets = Insets(25, 0, 5, 0)
        })
    }

    private fun addClearButton(y: Int = Y++) {
        add(JButton("重置").apply {
            // 监听
            background = Color(0xB54747)
            addActionListener {
                val result = JOptionPane.showConfirmDialog(
                    this,
                    "是否清空所有内容?",
                    "误操作警告",
                    JOptionPane.YES_NO_OPTION
                )
                if (result == JOptionPane.YES_OPTION) {
                    log("重置")
                    SettingDataHandle(scmt).rest()
                }
            }
        }, c.apply {
            c.gridwidth = 0
            gridy = y
            gridx = 0
            insets = Insets(2, 0, 5, 0)
        })
    }

    private fun addExitButton(y: Int = Y++) {
        add(JButton("退出").apply {
            // 监听
            background = Color(0x3C3C3E)
            addActionListener {
                val result = JOptionPane.showConfirmDialog(
                    this,
                    "是否退出应用程序?",
                    "误操作警告",
                    JOptionPane.YES_NO_OPTION
                )
                if (result == JOptionPane.YES_OPTION) {
                    exitProcess(0)
                }
            }
        }, c.apply {
            c.gridwidth = 0
            gridy = y
            gridx = 0
            insets = Insets(2, 0, 28, 0)
        })
    }

    private fun addJSeparator(color: Color = Color(0x04A6E4B8), top: Int = 5, button: Int = 25, y: Int = Y++) {
        add(JSeparator().apply {
            foreground = color
        }, c.apply {
            c.gridwidth = 0
            gridy = y
            gridx = 0
            insets = Insets(top, 0, button, 0)
        })
    }

    private fun addLabel(label: String, y: Int = Y++) {
        add(JLabel(label), c.apply {
            c.gridwidth = 1
            gridy = y
            gridx = 0
            insets = Insets(5, 0, 5, 0)
        })
    }

    private fun addField(label: String, field: JTextField, y: Int = Y++) {
        c.gridwidth = 1
        c.gridx = 0
        c.gridy = y
        add(JLabel(label), c)
        c.gridx = 1
        c.insets = Insets(5, 0, 5, 0)
        add(field, c)
    }

    private fun addCheckBox(checkBox: JCheckBox, y: Int = Y++) {
        c.gridx = 0
        c.gridy = y
        c.insets = Insets(5, 0, 5, 0)
        add(checkBox, c)
    }

    private fun addFileChooserField(
        label: String,
        textField: JTextField,
        button: JButton,
        filter: FileNameExtensionFilter? = null,
        fileSelectionMode: Int = JFileChooser.FILES_ONLY,
        y: Int = Y++
    ) {
        c.gridwidth = 1
        c.gridx = 0
        c.gridy = y
        add(JLabel(label), c)
        c.gridx = 1
        add(textField, c)
        c.gridx = 2
        c.insets = Insets(5, 0, 5, 0)
        add(button, c)

        button.addActionListener {
            val fileChooser = JFileChooser()
            filter?.let { fileChooser.fileFilter = filter }
            // 设置文件选择模式，允许选择文件或文件夹
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                textField.text = fileChooser.selectedFile.path
            }
        }
    }
}
