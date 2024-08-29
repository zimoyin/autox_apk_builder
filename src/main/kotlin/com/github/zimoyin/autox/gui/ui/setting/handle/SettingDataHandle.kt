package com.github.zimoyin.autox.gui.ui.setting.handle

import com.github.zimoyin.autox.builder.addLogListener
import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.builder.setting.PermissionsSetting
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import com.github.zimoyin.autox.builder.setting.RunSetting
import com.github.zimoyin.autox.gui.ApkBuilderPojo
import com.github.zimoyin.autox.gui.Result
import com.github.zimoyin.autox.gui.ui.NotificationWindows
import com.github.zimoyin.autox.gui.ui.console.Console
import com.github.zimoyin.autox.gui.ui.setting.SettingComponent
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2024/08/28
 */
class SettingDataHandle(private val scmt: SettingComponent) {

    fun handle() = scmt.apply {
        kotlin.runCatching {
            check()
        }.onFailure {
            NotificationWindows.error(it.message)
            return@apply
        }


        val console = Console()
        addLogListener {
            console.logArea(it.message)
        }
        console.createConsole()

        log("Start Building APK")

        // 装配 Result
        Thread {
            val config = buildApkBuilderPojo(scmt)
            val projectBean = buildProjectJsonBean(scmt)
            Result(config, projectBean).apply {
                onError = {
                    NotificationWindows.error(it.message)
                    it.printStackTrace()
                    config.deleteCache()
                }
                onSuccess = {
                    NotificationWindows.info("Build APK Success")
                    Result.openFileExplorer(it.parent)
                    config.deleteCache()
                }
                build()
            }
        }.start()
    }


    fun buildApkBuilderPojo(scmt: SettingComponent) = ApkBuilderPojo(
        gui = true,
        assets = listOf(scmt.assetsField.text),
        projectJson = scmt.projectJsonField.text,
        workDir = scmt.workDirField.text,
        iconPath = scmt.appIconTextField.text,
        startIconPath = scmt.splashImageTextField.text,
        templateApkPath = scmt.templateApkPathField.text,
        signatureFile = scmt.signatureFileTextField.text,
        signatureAlias = scmt.signatureAliasField.text,
        signaturePassword = scmt.signaturePasswordField.text,
    )

    fun buildProjectJsonBean(
        scmt: SettingComponent,
        bean: ProjectJsonBean = ProjectJsonBean.findFile(scmt.projectJsonField.text)
    ) = ProjectJsonBean(
        abis = bean.abis,
        assets = bean.assets,
        buildDir = bean.buildDir,
        build = bean.build,
        useFeatures = bean.useFeatures,
        icon = scmt.appIconTextField.text,
        ignoredDirs = bean.ignoredDirs,
        launchConfig = buildRunSetting(scmt),
        libs = scmt.libs,
        main = bean.main,
        name = scmt.appNameField.text,
        outputPath = bean.outputPath,
    )

    private fun buildRunSetting(scmt: SettingComponent) = RunSetting(
        hideLogs = scmt.hideLogCheckBox.isSelected,
        hideLauncher = scmt.hideDesktopIconCheckBox.isSelected,
        stableMode = scmt.stableModeCheckBox.isSelected,
        hideAccessibilityServices = scmt.removeAccessibilityCheckBox.isSelected,
        displaySplash = scmt.displaySplashCheckBox.isSelected,
        splashIcon = scmt.splashImageTextField.text,
        splashText = scmt.startTextTextField.text,
        serviceDesc = scmt.accessibilityDescriptionTextField.text,
        permissions = HashSet<PermissionsSetting>().apply {
            if (scmt.requestAccessibilityCheckBox.isSelected) {
                add(PermissionsSetting.ACCESSIBILITY_SERVICES)
            }
            if (scmt.requestOverlayPermissionCheckBox.isSelected) {
                add(PermissionsSetting.DRAW_OVERLAY)
            }

            if (scmt.requestBackgroundPopupPermissionCheckBox.isSelected) {
                add(PermissionsSetting.BACKGROUND_START)
            }
        }
    )

    /**
     * 检查必要的信息是否填写
     */
    private fun check() = scmt.apply {
        if (assetsField.text.isEmpty()) {
            throw Exception("assets 不能为空")
        }

        if (File(assetsField.text).exists().not()) {
            throw Exception("assets 不是一个正确的文件夹")
        }
        if (projectJsonField.text.isEmpty()) {
            throw Exception("project.json 路径不能为空")
        }
        if (File(projectJsonField.text).exists().not()) {
            throw Exception("project.json 不存在")
        }
        if (appNameField.text.isEmpty()) {
            throw Exception("应用名称不能为空")
        }
        if (packageNameField.text.isEmpty()) {
            throw Exception("包名不能为空")
        }
        if (versionNameField.text.isEmpty()) {
            throw Exception("版本名称不能为空")
        }
        if (versionCodeField.text.isEmpty()) {
            throw Exception("版本号不能为空")
        }
        try {
            versionCodeField.text.toInt()
        } catch (e: Exception) {
            throw Exception("版本号必须为数字")
        }
    }


    fun rest() = scmt.apply {
        workDirField.text = ""

        projectJsonField.text = ""
        packageNameField.text = ""
        assetsField.text = ""
        templateApkPathField.text = ""
        appNameField.text = ""
        packageNameField.text = ""
        versionNameField.text = ""
        versionCodeField.text = ""
        splashImageTextField.text = ""
        appIconTextField.text = ""
        signatureFileTextField.text = ""
        signatureAliasField.text = ""
        signaturePasswordField.text = ""
        accessibilityDescriptionTextField.text = ""
        startTextTextField.text = ""
        libTerminalCheckBox.isSelected = true
        libOpencvCheckBox.isSelected = false
        libPaddleOcrCheckBox.isSelected = false
        libcxxSharedSoCheckBox.isSelected = false
        libTesseractCheckBox.isSelected = false
        lib7zipCheckBox.isSelected = false
        hideDesktopIconCheckBox.isSelected = false
        stableModeCheckBox.isSelected = false
        hideLogCheckBox.isSelected = false
        volumeKeyEndTaskCheckBox.isSelected = false
        requestAccessibilityCheckBox.isSelected = false
        removeAccessibilityCheckBox.isSelected = false
        requestOverlayPermissionCheckBox.isSelected = false
        requestBackgroundPopupPermissionCheckBox.isSelected = false
        libcxxSharedSoCheckBox.isSelected = false
        libPaddleLightApiSharedSoCheckBox.isSelected = false
        libHiaiSoCheckBox.isSelected = false
        libHiaiIrSoCheckBox.isSelected = false
        libHiaiIrBuildSoCheckBox.isSelected = false
        libMlkitGoogleOcrPipelineSoCheckBox.isSelected = false
        libTesseractSoCheckBox.isSelected = false
        libPngSoCheckBox.isSelected = false
        libJpegSoCheckBox.isSelected = false
        libleptonicaSoCheckBox.isSelected = false
        libP7zipSoCheckBox.isSelected = false
    }
}