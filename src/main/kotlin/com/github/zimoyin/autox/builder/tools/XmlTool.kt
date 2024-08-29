package com.github.zimoyin.autox.builder.tools

import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultAttribute
import java.io.File
import java.io.FileWriter

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
class XmlTool(private val projectData: ProjectJsonBean) {
    fun modify(manifestFile: File, iconPath: String? = null): Document {
        // 读取 AndroidManifest.xml 文件
        val reader = SAXReader()
        val document: Document = reader.read(manifestFile)

        // 修改 manifest 节点属性
        val manifestElement: Element = document.rootElement
        manifestElement.attribute("package").value = projectData.packageName
        manifestElement.attribute("versionCode").let {
            if (it == null) manifestElement.setOrCreateAttribute(
                "android:versionCode",
                projectData.versionCode.toString()
            )
        }


        // 修改 application 节点属性
        val applicationElement: Element = manifestElement.element("application")
        applicationElement.attribute("label").let {
            if (it == null) manifestElement.setOrCreateAttribute(
                "android:label",
                projectData.versionCode.toString()
            )
            else if (it.value != null) it.value = projectData.name
        }
        if (iconPath != null){
            applicationElement.attribute("icon").let {
                if (it == null) manifestElement.setOrCreateAttribute(
                    "android:icon",
                    "@drawable/application"
                )
                else if (it.value != null) it.value = "@drawable/application"
            }
        }
//        applicationElement.setOrCreateAttribute("label", projectData.name)

        // TODO 暂时不替换name
//        applicationElement.attribute("name")?.let {
//            if (it.value != null) it.value = it.value.replace("com.stardust.auojs.inrt", projectData.packageName)
//        }

        // 修改 service 节点属性
        applicationElement.elements("service").forEach { serviceElement ->
            if (serviceElement.attribute("name")?.value == "com.stardust.notification.NotificationListenerService") {
                println(projectData.launchConfig.hideAccessibilityServices)
                serviceElement.attribute("label")?.let {
                    if (it.value != null) it.value = projectData.name
                }
            }
            // 隐藏无障碍模式
            if (projectData.launchConfig.hideAccessibilityServices) {
                if (serviceElement.attribute("permission")?.value == "android.permission.BIND_ACCESSIBILITY_SERVICE") {
                    serviceElement.attribute("permission")?.value = ""
                }
            }
        }

        // 修改 provider 节点属性
        applicationElement.elements("provider").forEach { providerElement ->
            providerElement.attribute("authorities")?.let {
//                if (it.value != null) it.value = it.value.replace("com.stardust.auojs.inrt", projectData.packageName)
                if (it.value != null) it.value = "${projectData.packageName}.fileprovider"
            }
        }

        // 错误修改代码，在确定无用后删除
        // 修改 activity 节点属性
//        applicationElement.elements("activity").forEach { activity ->
//            activity.attribute("name")?.let {
//                if (it.value != null) it.value = it.value.replace("com.stardust.auojs.inrt", projectData.packageName)
//            }
//        }

        // 写回修改后的 XML 文件
        return document
    }

    private fun Element.setOrCreateAttribute(attributeName: String, value: String) {
        var attribute = attribute(attributeName)
        if (attribute == null) {
            attribute = DefaultAttribute(attributeName, value)
            add(attribute)
        } else {
            attribute.value = value
        }
    }
}

fun Document.writeToFile(filePath: String) {
    val writer = XMLWriter(FileWriter(filePath))
    kotlin.runCatching { writer.write(this) }
    writer.close()
}