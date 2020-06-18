package com.droid.brightness.runner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.droid.brightness.analyzer.BrightnessAnalyzer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BrightnessAnalyzer().add(50, 8)
    }
}