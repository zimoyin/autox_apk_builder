package com.github.zimoyin.autox.builder

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
fun log(message: String) {
    println("[Autox_Build_Apk] $message")
    for (listener in listeners) {
        listener(Log(message,listener))
    }
}

private val listeners = mutableListOf<(Log) -> Unit>()
fun addLogListener(listener: (Log) -> Unit) {
    log("添加日志监听器")
    listeners.add(listener)
}

data class Log(val message: String, private val listener: (Log) -> Unit) {
    fun close() {
        log("关闭日志监听器")
        listeners.removeIf {
            it == listener
        }
    }
}