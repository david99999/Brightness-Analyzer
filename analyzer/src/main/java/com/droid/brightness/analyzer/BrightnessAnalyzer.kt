package com.droid.brightness.analyzer

public class BrightnessAnalyzer {
    fun add(a: Int, b:Int): Int{
        return a+b;
    }

    external fun getHistogram(
        frame: ByteArray,
        width: Int,
        height: Int,
        samplingFactor: Int,
        pixelsProcessed: IntArray
    ): IntArray


    companion object {
        init {
            System.loadLibrary("native-analyzer")
        }
    }
}