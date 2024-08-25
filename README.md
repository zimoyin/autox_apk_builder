# Autox 打包工具
Autox 是基于一个 template.apk 的模板进行打包的，可以通过修改 template.apk 来实现打包。


## API
引入依赖
```kotlin
repositories {
    mavenCentral()
    google()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.zimoyin:autojsx_builder:1.0.2")
    implementation("com.github.zimoyin:autojsx_builder:1.0.2:apksigner")
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