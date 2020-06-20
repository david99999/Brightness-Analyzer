package com.droid.brightness.runner

import android.util.Log
import androidx.annotation.IntDef
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.droid.brightness.runner.Utils.Companion.YUV420888ToNv21
import com.droid.brightness.analyzer.BrightnessAnalyzer as NativeAnalyzer

/**
 * Analyzer that decomposes {@link ImageProxy} into a 256 IntArray containing an average
 * representation of the brightness on the image, if an image is bright it will contain
 * more values on the 240~255 positions of the @property histogram, and for dark images
 * the lower positions (0~10) will contain more values
 **/
class BrightnessAnalyzer(private var listener: BrightnessListener) : ImageAnalysis.Analyzer {
    val TAG = "BrightnessAnalyzer"
    override fun analyze(image: ImageProxy) {
        val pixelsProcessed = IntArray(1)
        val histogram = NativeAnalyzer.getHistogram( // Calling native brightness calculation
            YUV420888ToNv21(image),
            image.width,
            image.height,
            2,
            pixelsProcessed
        )
        val darkRange = IntRange(0, 10) // Dark values are contained in this range in the histogram
        val normalRange = IntRange(11, 249) // Normal colors are the most broad part
        val brightRange = IntRange(250, 255) // Bright colors are mostly on 5 last positions
        val ranges = arrayListOf(0, 0, 0)
        for (index in histogram.indices) {
            when (index) {
                in darkRange -> ranges[0] += histogram[index]
                in normalRange -> ranges[1] += histogram[index]
                else -> ranges[2] += histogram[index]
            }
        }
        ranges[0] /= (darkRange.last - darkRange.first) // dark average
        ranges[1] /= (normalRange.last - normalRange.first) // normal average
        ranges[2] /= (brightRange.last - brightRange.first) // bright average
        listener.onBrightnessAnalyzed(histogram, ranges.indexOf(ranges.max()))
        image.close()
    }

    companion object {
        @IntDef(DARK, NORMAL, BRIGHT)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Brightness

        const val DARK = 0
        const val NORMAL = 1
        const val BRIGHT = 2
    }
}