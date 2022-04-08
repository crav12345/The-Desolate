package com.chrisravosa.thedesolate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    // Used to animate character.
    private lateinit var survivorIdleAnimation: AnimationDrawable

    // Variables to access hardware.
    private lateinit var sensorManager: SensorManager
    private var mSensor: Sensor? = null
    private var tSensor: Sensor? = null

    /** Called as activity is started */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize sensor manager and sensors.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        tSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        // Check for existence of various sensors.
        if (mSensor == null) {
            // Can't handle enemies, so can't play the game.
            findViewById<Button>(R.id.button).isEnabled = false
        }
        if (tSensor == null) {
            // Disable dynamic sprite outfits.
        }

        // Write top scores to local storage
        saveTopScores()
    }

    /** Called as activity becomes visible to user */
    override fun onStart() {
        super.onStart()

        // An animation_list can't be called in onCreate(), so add the
        // 8-bit-bounce to our player character here.
        findViewById<ImageView>(R.id.survivorImage).apply {
            setBackgroundResource(R.drawable.survivor_idle_right)
            survivorIdleAnimation = background as AnimationDrawable
        }

        survivorIdleAnimation.start()
    }

    /** Called when the user taps the 'Start' button */
    fun startGameActivity(view: View) {
        // Load GameActivity
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the 'High Scores' button */
    fun startHighScoresActivity(view: View) {
        // Load HighScoresActivity
        val intent = Intent(this, HighScores::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the 'Quit' button */
    fun quit(view: View) {
        // Terminate the program
        finishAffinity()
    }

    fun saveTopScores() {
        // Write score to local storage.
        val prefs = getSharedPreferences("highScores", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt("score1", 0)
        editor.putInt("score2", 0)
        editor.putInt("score3", 0)
        editor.putInt("score4", 0)
        editor.putInt("score5", 0)
        editor.putInt("score6", 0)
        editor.putInt("score7", 0)
        editor.putInt("score8", 0)
        editor.putInt("score9", 0)
        editor.putInt("score10", 0)
        editor.commit()
    }
}