package com.mantracounter

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import android.widget.EditText

class MainActivity : ComponentActivity() {

    private val PREFS = "mantra_prefs"
    private val KEY_COUNT = "count"
    private val KEY_TARGET = "target"

    private var count = 0
    private var target = 108

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // load prefs
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        count = prefs.getInt(KEY_COUNT, 0)
        target = prefs.getInt(KEY_TARGET, 108)

        val tvCount = findViewById<TextView>(R.id.tvCount)
        val tvTarget = findViewById<TextView>(R.id.tvTarget)
        val btnInc = findViewById<Button>(R.id.btnInc)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnSetTarget = findViewById<Button>(R.id.btnSetTarget)

        fun updateViews() {
            tvCount.text = count.toString()
            tvTarget.text = "Target: $target"
        }

        updateViews()

        btnInc.setOnClickListener {
            count += 1
            prefs.edit().putInt(KEY_COUNT, count).apply()
            updateViews()
            if (count == target) {
                // vibrate
                val vibrator = getSystemService<Vibrator>()
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                // play default notification sound
                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(applicationContext, notification)
                r.play()
                // show dialog
                AlertDialog.Builder(this)
                    .setTitle("Target reached!")
                    .setMessage("You completed $target repetitions.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        btnReset.setOnClickListener {
            count = 0
            prefs.edit().putInt(KEY_COUNT, count).apply()
            updateViews()
        }

        btnSetTarget.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set Target")
            val input = EditText(this)
            input.hint = "Enter number (e.g. 108)"
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            builder.setView(input)
            builder.setPositiveButton("Save") { _, _ ->
                val num = input.text.toString().toIntOrNull()
                if (num != null && num > 0) {
                    target = num
                    prefs.edit().putInt(KEY_TARGET, target).apply()
                    updateViews()
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }
    }
}
