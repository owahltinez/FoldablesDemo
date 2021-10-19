package com.example.foldablesdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    private var currentMetrics: WindowMetrics? = null
    private var layoutInfo: WindowLayoutInfo? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val wmInfo = windowInfoRepository()
        val calculator = WindowMetricsCalculator.getOrCreate()

        GlobalScope.launch {
            wmInfo.currentWindowMetrics.collect {
                currentMetrics = it
                updateDisplay(calculator)
            }
        }
        GlobalScope.launch {
            wmInfo.windowLayoutInfo.collect {
                layoutInfo = it
                updateDisplay(calculator)
            }
        }
    }

    @Synchronized
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