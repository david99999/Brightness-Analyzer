package com.droid.brightness.runner

import android.Manifest
import android.R.color.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import com.droid.brightness.runner.BrightnessAnalyzer.Companion.Brightness
import com.droid.brightness.runner.BrightnessAnalyzer.Companion.DARK
import com.droid.brightness.runner.BrightnessAnalyzer.Companion.NORMAL
import com.droid.brightness.runner.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private var content: ActivityMainBinding? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = DataBindingUtil.setContentView(this, R.layout.activity_main)
        content!!.view = this
        checkCameraPerms()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun checkCameraPerms() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCamera() {
        content?.hasCameraPerm = true
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor, BrightnessAnalyzer(object : BrightnessListener {
                        override fun onBrightnessAnalyzed(
                            histogram: IntArray,
                            @Brightness result: Int
                        ) {
                            histogramView.setData(histogram)
                            runOnUiThread {
                                textMainContent.apply {
                                    when (result) {
                                        DARK -> {
                                            text = getString(R.string.dark_image)
                                            setTextColor(getColor(context, white))
                                        }
                                        NORMAL -> {
                                            text = getString(R.string.normal_image)
                                            setTextColor(getColor(context, holo_blue_dark))
                                        }
                                        else -> {
                                            text = getString(R.string.bright_image)
                                            setTextColor(getColor(context, holo_red_dark))
                                        }
                                    }
                                }
                            }
                        }
                    }))
                }
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            try {
                cameraProvider.unbindAll()
                camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(rc: Int, perms: Array<String>, granted: IntArray) {
        if (rc == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}