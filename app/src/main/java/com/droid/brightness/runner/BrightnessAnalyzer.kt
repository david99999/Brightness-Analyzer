package com.droid.brightness.runner

import android.util.Log
import androidx.annotation.IntDef
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.droid.brightness.analyzer.BrightnessAnalyzer as NativeAnalyzer


class BrightnessAnalyzer(private var listener: BrightnessListener) : ImageAnalysis.Analyzer {
    val TAG = "BrightnessAnalyzer"
    override fun analyze(image: ImageProxy) {
        val pixelsProcessed = IntArray(1)
        val histogram = NativeAnalyzer.getHistogram(
            Utils.yuv420888ToNv21(image),
            image.width,
            image.height,
            2,
            pixelsProcessed
        )
        val darkRange = IntRange(0, 10)
        val normalRange = IntRange(11, 249)
        val brightRange = IntRange(250, 255)
        val sections = arrayListOf(0, 0, 0)
        for (index in histogram.indices) {
            when (index) {
                in darkRange -> sections[0] += histogram[index]
                in normalRange -> sections[1] += histogram[index]
                else -> sections[2] += histogram[index]
            }
        }
        sections[0] /= (darkRange.last - darkRange.first) // dark average
        sections[1] /= (normalRange.last - normalRange.first) // normal average
        sections[2] /= (brightRange.last - brightRange.first) // bright average
        Log.i(TAG, sections.toString())
        listener.onBrightnessAnalyzed(histogram, sections.indexOf(sections.max()))
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