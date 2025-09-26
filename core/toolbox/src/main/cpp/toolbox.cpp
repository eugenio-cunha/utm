#define STB_IMAGE_IMPLEMENTATION
#include "header/stb_image.h"

#include <jni.h>
#include <string>
#include "header/turbojpeg.h"
#include <android/log.h>

#define LOG_TAG "ImageProcessor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_br_com_b256_core_toolbox_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_br_com_b256_core_toolbox_NativeLib_compress(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray input) {

    // Obtem dados de entrada
    jsize inputLength = env->GetArrayLength(input);
    if (inputLength <= 0) return nullptr;

    jbyte *srcBuf = env->GetByteArrayElements(input, nullptr);

    // Supondo imagem RGB de tamanho fixo 1920x1080
    const int width = 2268;
    const int height = 4032;
    const int pixelSize = 3; // RGB
    const int expectedSize = width * height * pixelSize;

    if (inputLength < expectedSize) {
        env->ReleaseByteArrayElements(input, srcBuf, JNI_ABORT);
        return nullptr;
    }

    // Inicializa libjpeg-turbo
    tjhandle handle = tjInitCompress();
    if (!handle) {
        env->ReleaseByteArrayElements(input, srcBuf, JNI_ABORT);
        return nullptr;
    }

    unsigned char *jpegBuf = nullptr;
    unsigned long jpegSize = 0;

    int result = tjCompress2(
            handle,
            reinterpret_cast<unsigned char *>(srcBuf),  // srcBuffer
            width,
            0, // pitch = 0 = width * pixelSize
            height,
            TJPF_RGB,  // pixel format
            &jpegBuf,
            &jpegSize,
            TJSAMP_420,
            85,             // quality (0–100)
            TJFLAG_FASTDCT    // flags (melhor performance)
    );

    tjDestroy(handle);
    env->ReleaseByteArrayElements(input, srcBuf, JNI_ABORT);

    if (result != 0 || jpegBuf == nullptr) {
        if (jpegBuf) tjFree(jpegBuf);
        return nullptr;
    }

    // Copia o buffer JPEG para jbyteArray
    jbyteArray output = env->NewByteArray(static_cast<jsize>(jpegSize));
    if (output != nullptr) {
        env->SetByteArrayRegion(output, 0, jpegSize, reinterpret_cast<jbyte *>(jpegBuf));
    }

    tjFree(jpegBuf);
    return output;
}
