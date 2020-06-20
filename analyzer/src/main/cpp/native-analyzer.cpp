#include <jni.h>
#include <string>
#include <cmath>
#include <android/log.h>

#define APP_NAME "nativeAnalyzer"
#define LOGI(...)    __android_log_print(ANDROID_LOG_INFO,APP_NAME,__VA_ARGS__)
/**
 * structure defined for the colors (ARGB)
 */
typedef struct {
    uint8_t alpha;
    uint8_t red;
    uint8_t green;
    uint8_t blue;
} argb;

extern "C" {
/**
 * Detects the brightness and darkness on a nv21 frame
 * _00024  due to this method will be called from a companion object and because it is represented as
 * an inner class the class path will become BrightnessAnalyzer$Companion
 *
 * @param frame The NV21 Frame to be analyzed
 * @param width The Frame width
 * @param height The Frame height
 * @param samplingFactor The number of pixels to skip when counting average brightness
 * @param pixelsProcessed Will contain the number if pixels processed after applying #samplingFactor
 * @return A 256 IntArray containing the average black/white color values, Ex:
 *
 * [0]  -> 0
 * [1]  -> 0
 * [2]  -> 0
 * ...
 * [255]-> 2000
 */

JNIEXPORT jintArray JNICALL
Java_com_droid_brightness_analyzer_BrightnessAnalyzer_00024Companion_getHistogram(JNIEnv *env,
                                          jobject obj,
                                          jbyteArray frame,
                                          jint width,
                                          jint height,
                                          jint samplingFactor,
                                          jintArray pixelsProcessed) {

    LOGI("c++ analyzing frame byte array");
    jintArray histogram = env->NewIntArray(256);
    jbyte *bytes = env->GetByteArrayElements(frame, 0);
    jint *integers = env->GetIntArrayElements(histogram, 0);
    jint *totalPixels = env->GetIntArrayElements(pixelsProcessed, 0);
//        LOGI("iterating through byte array");
    int total = width * height;
    int Y, Cb = 0, Cr = 0;
    int R, G, B;
    int samplingFactorPow = (int) pow(samplingFactor, 0.5);
    totalPixels[0] = 0;
//        LOGI("subsampling at %d",samplingFactorPow);
    for (int y = 0; y < height; y += samplingFactorPow) {
        for (int x = 0; x < width; x += samplingFactorPow) {
            Y = bytes[y * width + x];
            if (Y < 0) Y += 255;
            if ((x & 1) == 0) {
                Cr = bytes[(y >> 1) * (width) + x + total];
                Cb = bytes[(y >> 1) * (width) + x + total + 1];
                if (Cb < 0) Cb += 127; else Cb -= 128;
                if (Cr < 0) Cr += 127; else Cr -= 128;
            }
//                R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
//                G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
//                B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
            // Approximation
            R = (int) (Y + 1.40200 * Cr);
            G = (int) (Y - 0.34414 * Cb - 0.71414 * Cr);
            B = (int) (Y + 1.77200 * Cb);

            if (R < 0) R = 0; else if (R > 255) R = 255;
            if (G < 0) G = 0; else if (G > 255) G = 255;
            if (B < 0) B = 0; else if (B > 255) B = 255;

            int value = (R + G + B) / 3;
//                LOGI("RGB->%d,%d,%d,R2G2B2->%d,%d,%d", R,G,B,R2,G2,B2);
            integers[value]++;
            totalPixels[0]++;
        }
    }
//        LOGI("passed %d times", totalPixels[0]);
//        LOGI("creating java int array for total pixels processed");
    env->SetIntArrayRegion(histogram, 0, 256, integers);
//        LOGI("creating java int array");
    env->SetIntArrayRegion(pixelsProcessed, 0, 1, totalPixels);
//        LOGI("releasing native jint array");
    env->ReleaseIntArrayElements(histogram, integers, NULL);
//        LOGI("releasing native  jint array for total pixels processed");
    env->ReleaseIntArrayElements(pixelsProcessed, totalPixels, NULL);
//        LOGI("releasing native jbyte array");
    env->ReleaseByteArrayElements(frame, bytes, NULL);
    LOGI("c++ returning java int array");
    return histogram;
}

}