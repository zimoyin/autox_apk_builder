package com.github.zimoyin.autox.builder.tools

import com.github.zimoyin.autox.builder.log
import java.io.File

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
class ApkTool {
    companion object{

        /**
         * 构建解码命令
         * @param apkPath apk文件路径
         * @param outputPath 解码后的apk文件路径
         */
        fun decodeApk(apkPath: String, outputPath: String? = null) {
            if (!checkPath(apkPath)) {
                throw IllegalArgumentException("apk文件路径不存在或者包含非法字符（ASCII码范围：0~127 之外的字符）")
            }
            val output = outputPath ?: apkPath.replace(".apk", "_decoded.apk")
            val command = arrayOf("d", "-f", "-s", "-o", output, apkPath)
            log("decodeApk command: ${command.joinToString(" ")}")
            brut.apktool.Main.main(command)
        }

        fun encodeApk(path: String, outputPath: String? = null) {
            if (!checkPath(path)) {
                throw IllegalArgumentException("apk文件路径不存在或者包含非法字符（ASCII码范围：0~127 之外的字符）")
            }
            val output = outputPath ?: path.replace("_decoded.apk", "_encoded.apk")
            val command = arrayOf("b", "-f", "-o", output, path)
            log("encodeApk command: ${command.joinToString(" ")}")
            brut.apktool.Main.main(command)
        }

        /**
         * 检查路径，文件或者文件夹是否存在，全部路径中是否完全符合 ascii 编码，不包含其他字符
         */
        private fun checkPath(path: String): Boolean {
            if (!path.matches(Regex("^[\\x00-\\x7F]+$"))) {
                return false
            }
            return File(path).exists()
        }
    }
}