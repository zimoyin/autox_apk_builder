# Autox 打包工具
Autox 是基于一个 template.apk 的模板进行打包的，可以通过修改 template.apk 来实现打包。

## 运行
### A. 界面运行
```shell
java -jar xxx.jar 
// java -jar xxx.jar -createConfig
// java -jar xxx.jar xxxx配置文件路径.json
```

### B. 无界面运行
1. 无指定配置文件运行
```shell
# 第一次运行后，在当前文件夹生成 apk_builder_config.json 配置文件请填写它
java -jar xxx.jar -noGui -createConfig
```
2. 指定配置文件运行
```shell
# 第一次运行后，在当前文件夹生成 apk_builder_config.json 配置文件请填写它
java -jar xxx.jar -noGui xxxx配置文件路径.json
```
### C. 配置文件
```
assets： 存放源码，库，资源文件的地方。他是一个字符串数组。当指定后会复制指定路径下的文件到临时命令
templateApkPath： 模板 apk 的路径。默认为 null
startIconPath： 启动图标的绝对路径。默认为 null
projectJson： 项目的 json 文件路径。默认为 null。如果不指定会在 assets 中搜索
gui： 是否使用gui 界面。默认为 true
workDir： 工作目录。默认为 临时目录
iconPath： 项目图标的绝对路径。默认为 null
```
## API
引入依赖  
_该仓库使用LSF 托管了大文件，所以 jitpack 无法正常的构建。因此构建的项目来自于可执行库(autojsx_builder)[https://github.com/zimoyin/autojsx_builder]_
```kotlin
repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.zimoyin:autojsx_builder:1.0.3.fix2")
    implementation("com.github.zimoyin:autojsx_builder:1.0.3.fix2:apksigner")
    testImplementation(kotlin("test"))
}

```

示例
```kotlin
val src = "out/intermediate_compilation_files"

AutoxApkBuilder()
    .setAssets(src)
    .setIconPath("C:\\Users\\zimoa\\Desktop\\favicon.ico")
    .setStartIconPath("C:\\Users\\zimoa\\Pictures\\111075413_p0.png")
    .build()
    .apply {
        copyDest(File("out/build"))
    }
```