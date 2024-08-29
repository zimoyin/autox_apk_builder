import com.github.zimoyin.autox.builder.AutoxApkBuilder
import com.github.zimoyin.autox.builder.copyDest
import com.github.zimoyin.autox.builder.setting.ProjectJsonBean
import java.io.File

/**
 *
 *
 * 打包后将插件放到 GITHUB
 * idea 通过(允许使用代理网络)网络下载插件
 * 插件存储位置当前顶层的项目中的 gradle 文件夹下，没有文件夹就创建
 *
 *
 */
fun main() {
    val src = "out/intermediate_compilation_files"

    ProjectJsonBean.findFile("project.json")
    AutoxApkBuilder()
        .setWorkDir(File(""))
        .setAssets(src)
        .setIconPath("C:\\Users\\zimoa\\Desktop\\favicon.ico")
        .setStartIconPath("C:\\Users\\zimoa\\Pictures\\111075413_p0.png")
        .build()
        .apply {
            copyDest(File("out/build"))
        }
}