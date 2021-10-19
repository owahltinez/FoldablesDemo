package com.example.foldablesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.window.java.layout.WindowInfoRepositoryCallbackAdapter
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    private var currentMetrics: WindowMetrics? = null
    private var layoutInfo: WindowLayoutInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Initialize window-related objects
        val wmInfo = windowInfoRepository()
        val calculator = WindowMetricsCalculator.getOrCreate()

        // Use a single-threaded executor for the callbacks
        val executor = Executors.newSingleThreadExecutor()

        // Register callbacks for window property changes
        val adapter = WindowInfoRepositoryCallbackAdapter(wmInfo)
        adapter.addCurrentWindowMetricsListener(executor) {
            currentMetrics = it
            updateDisplay(calculator)
        }
        adapter.addWindowLayoutInfoListener(executor) {
            layoutInfo = it
            updateDisplay(calculator)
        }
    }

    private fun updateDisplay(calculator: WindowMetricsCalculator) {
        // Build the output text incrementally
        val sb = StringBuilder()

        // Get the current fold state
        sb.append("${layoutInfo?.displayFeatures?.toString()}\n")

        // Get the maximum window metrics
        val maxMetrics = calculator.computeMaximumWindowMetrics(this@MainActivity)
        sb.append("MaximumWindowMetrics: ${maxMetrics.bounds.flattenToString()}\n")

        // Get the current window metrics
        sb.append("CurrentWindowMetrics: ${currentMetrics?.bounds?.flattenToString()}\n")

        // Update the UI with the new information
        runOnUiThread { textView.text = sb.toString() }
    }
}