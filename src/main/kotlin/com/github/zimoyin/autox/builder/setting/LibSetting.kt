package com.github.zimoyin.autox.builder.setting

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

/**
 *
 * @author : zimo
 * @date : 2024/08/24
 */
@JsonRootName("libs")
class LibSetting : HashSet<LibItem>()

/**
 * libjackpal-androidterm5.so 和 libjackpal-termexec2.so - 这两个库可能一起使用，用于实现 Android 终端仿真和执行功能。
 * libpaddle_light_api_shared.so - PaddleOCR 轻量级 API 库。通常需要 PaddleOCR 的模型文件，可能还需要 libopencv_java4.so 来进行图像预处理。
 * libhiai.so, libhiai_ir.so, libhiai_ir_build.so - 这些是 HiAI 的库，可能彼此依赖以实现完整的 AI 推理功能。
 * libleptonica.so - 图像处理库，通常与 libtesseract.so 一起使用，因为 Tesseract 依赖于 Leptonica 进行图像预处理。
 * libmlkit_google_ocr_pipeline.so - Google ML Kit OCR 库，可能依赖 libopencv_java4.so 或其他图像处理库。
 */
enum class LibItem{
    /**
     * Android Terminal Library
     */
    @JsonProperty("libjackpal-androidterm5.so")
    LIBJACKPAL_ANDROIDTERM5_SO,

    /**
     * Terminal Execution Library
     */
    @JsonProperty("libjackpal-termexec2.so")
    LIBJACKPAL_TERMEXEC2_SO,

    /**
     * OpenCV for Java (OpenCV)
     */
    @JsonProperty("libopencv_java4.so")
    LIBOPENCV_JAVA4_SO,

    /**
     * C++ Shared Library
     */
    @JsonProperty("libc++_shared.so")
    LIBCXX_SHARED_SO,

    /**
     * PaddleOCR Lightweight API (PaddleOCR)
     */
    @JsonProperty("libpaddle_light_api_shared.so")
    LIBPADDLE_LIGHT_API_SHARED_SO,

    /**
     * HiAI (Huawei AI) Library
     */
    @JsonProperty("libhiai.so")
    LIBHIAI_SO,

    /**
     * HiAI IR (Intermediate Representation) Library
     */
    @JsonProperty("libhiai_ir.so")
    LIBHIAI_IR_SO,

    /**
     * HiAI IR Build Library
     */
    @JsonProperty("libhiai_ir_build.so")
    LIBHIAI_IR_BUILD_SO,

    /**
     * Native C++ Library
     */
    @JsonProperty("libNative.so")
    LIBNATIVE_SO,

    /**
     * Google ML Kit OCR Library (Google ML Kit OCR)
     */
    @JsonProperty("libmlkit_google_ocr_pipeline.so")
    LIBMLKIT_GOOGLE_OCR_PIPELINE_SO,

    /**
     * Tesseract OCR Library (TesseractOCR)
     */
    @JsonProperty("libtesseract.so")
    LIBTESSERACT_SO,

    /**
     * PNG Image Processing Library
     */
    @JsonProperty("libpng.so")
    LIBPNG_SO,

    /**
     * Leptonica Image Processing Library
     */
    @JsonProperty("libleptonica.so")
    LIBLEPTONICA_SO,

    /**
     * JPEG Image Processing Library
     */
    @JsonProperty("libjpeg.so")
    LIBJPEG_SO,

    /**
     * 7zip Compression Library (7zip)
     */
    @JsonProperty("libp7zip.so")
    LIBP7ZIP_SO,

}