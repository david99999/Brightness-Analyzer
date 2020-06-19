package com.droid.brightness.runner

import com.droid.brightness.runner.BrightnessAnalyzer.Companion.Brightness

interface BrightnessListener {
    fun onBrightnessAnalyzed(histogram: IntArray, @Brightness result: Int)
}