package com.chrisravosa.thedesolate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class EnterScore : AppCompatActivity() {
    /** Called as activity is started */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_score)

        // Intent extras passed from GameActivity.
        val daysSurvived=intent.getIntExtra("daysSurvived", 0)
        val areasVisited=intent.getIntExtra("areasVisited", 0)
        val score=intent.getIntExtra("score", 0)

        // Set text for all HUD elements.
        findViewById<TextView>(R.id.textDaysSurvived).text =
            applicationContext.resources.getString(
                R.string.days_survived, daysSurvived
            )
        findViewById<TextView>(R.id.textAreasExplored).text =
            applicationContext.resources.getString(
                R.string.rooms_explored, areasVisited
            )
        findViewById<TextView>(R.id.textScore).text =
            applicationContext.resources.getString(
                R.string.game_score, score
            )
    }

    fun sendScore(view: View) {
        // Getting high score preferences.
        val prefs = getSharedPreferences("highScores", MODE_PRIVATE)

        // Intent extra passed from GameActivity.
        val score = intent.getIntExtra("score", 0)

        // Write score to local storage.
        val writePrefs = getSharedPreferences(
            "highScores",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = writePrefs.edit()
        editor.putInt("score", score)
        editor.commit()

        // Go back to MainActivity.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}