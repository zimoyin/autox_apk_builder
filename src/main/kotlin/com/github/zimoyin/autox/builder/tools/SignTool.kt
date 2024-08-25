package com.github.zimoyin.autox.builder.tools

import com.github.zimoyin.autox.builder.log
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
class SignTool {
    companion object{
        fun signApk(
            apkPath: String,
            signPath: String = "idea_plug_autox_wifi_zimo.jks",
            signAlias: String = "idea_plug_autox_wifi_zimo",
            password: String = "idea_plug_autox_wifi_zimo"
        ) {
            val signFile = File(signPath).apply {
                if (!exists()){
                    val resource = Thread.currentThread().contextClassLoader.getResourceAsStream(signPath)
                        ?: throw IllegalArgumentException("sign file not found")
                    createNewFile()
                    writeBytes(resource.readAllBytes())
                }
            }
            val command = arrayOf(
                "sign",
                "--ks", signFile.canonicalPath,
                "--ks-key-alias", signAlias,
                "--ks-pass", "pass:$password",
                apkPath
            )
            val commandForLogging = command.map {
                if (it.startsWith("pass:")) "pass:*****" else it
            }
            log("signApk command: ${commandForLogging.joinToString(" ")}")

            com.android.apksigner.ApkSignerTool.main(command)
        }
    }
}