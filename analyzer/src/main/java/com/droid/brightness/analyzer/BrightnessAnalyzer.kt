package com.droid.brightness.analyzer

/**
 * Kotlin wrapper to C++ code that detects the brightness and darkness
 * on a nv21 frame and returns it as a 256 IntArray
 */
class BrightnessAnalyzer {
    companion object {
        init {
            System.loadLibrary("native-analyzer")
        }

        /**
         * Detects the average brightness/darkness on the frame
         *
         * @param frame The NV21 Frame to be analyzed
         * @param width The Frame width
         * @param height The Frame height
         * @param samplingFactor The number of pixels to skip when counting average brightness
         * @param pixelsProcessed Will contain the number if pixels processed after applying #samplingFactor
         *
         * @return A 256 IntArray containing the average black/white color values, Ex:
         *
         * [0]  -> 0
         * [1]  -> 0
         * [2]  -> 0
         * ...
         * [255]-> 2000
         */
        external fun getHistogram(
            frame: ByteArray,
            width: Int,
            height: Int,
            samplingFactor: Int,
            pixelsProcessed: IntArray
        ): IntArray

    }
}