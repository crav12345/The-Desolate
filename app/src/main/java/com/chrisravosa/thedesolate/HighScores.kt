package com.chrisravosa.thedesolate

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class HighScores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)
    }

    override fun onStart() {
        super.onStart()

        // Getting high score preferences.
        val prefs = getSharedPreferences("highScores", MODE_PRIVATE)
        val score1 = prefs.getInt("score", 0)

        // Set text for all HUD elements.
        findViewById<TextView>(R.id.textScore10).text = score1.toString()
    }
}