package com.droid.brightness.runner

import com.droid.brightness.runner.BrightnessAnalyzer.Companion.Brightness

/**
 * Listener that notifies the analysis process has been completed
 */
interface BrightnessListener {
    fun onBrightnessAnalyzed(histogram: IntArray, @Brightness result: Int)
}