package com.github.zimoyin.autox.builder.setting

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 * 来自于 project.json 的 launchConfig 字段
 */
@JsonRootName("launchConfig")
data class RunSetting(
    /**
     * 隐藏桌面图标(通过adb或其他方法启动应用)
     */
    @JsonProperty("hideLauncher")
    val hideLauncher: Boolean = false,

    /**
     * 稳定模式
     */
    @JsonProperty("stableMode")
    val stableMode: Boolean = false,

    /**
     * 隐藏日志
     */
    @JsonProperty("hideLogs")
    val hideLogs: Boolean = false,

    /**
     * 音量上键结束任务
     */
    @JsonProperty("volumeUpcontrol")
    val volumeUpcontrol: Boolean = false,


    /**
     * 去掉无障碍服务
     */
    @JsonProperty("hideAccessibilityServices")
    val hideAccessibilityServices: Boolean = false,

    /**
     * 显示启动界面
     */
    @JsonProperty("displaySplash")
    val displaySplash: Boolean = true,

    /**
     * 启动界面图标
     */
    @JsonProperty("splashIcon")
    val splashIcon: String? = null,

    /**
     * 启动界面文字
     */
    @JsonProperty("splashText")
    val splashText: String = "Powered by Autoxjs.com. Packaged by IDEA plugin Autojsx WIFI provided",

    /**
     * 服务描述
     */
    val serviceDesc: String = "Autox WIFI",

    /**
     * 申请的权限
     */
    val permissions: HashSet<PermissionsSetting> = HashSet()
)

enum class PermissionsSetting {
    /**
     * 需要无障碍权限
     */
    @JsonProperty("accessibility_services")
    ACCESSIBILITY_SERVICES,

    /**
     * 需要后台弹出权限界面
     */
    @JsonProperty("background_start")
    BACKGROUND_START,

    /**
     * 需要悬浮窗权限
     */
    @JsonProperty("draw_overlay")
    DRAW_OVERLAY
}