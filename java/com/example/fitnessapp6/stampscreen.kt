package com.example.fitnessapp6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import java.time.LocalDateTime
import java.time.temporal.ChronoField;
import java.text.NumberFormat
import java.util.*




class stampscreen : AppCompatActivity(), SensorEventListener {
    // Added SensorEventListener the MainActivity class
    // Implement all the members in the class MainActivity
    // after adding SensorEventListener

    // we have assigned sensorManger to nullable
    private var sensorManager: SensorManager? = null

    // Creating a variable which will give the running status
    // and initially given the boolean value as false
    private var running = false

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    companion object {
        var totalSteps = 0f
        var previousTotalSteps = 0f
    }

    // Creating a variable  which counts previous total
    // steps and it has also been given the value of 0 float


    // Attempting to utilizes the below as a variable that will gather points based off of purchases.
    private var purchases = 0f

    private val current = LocalDateTime.now()
    private var currentTotalSteps: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.stamp_screen)

        currentTotalSteps = intent.getFloatExtra("currentTotalSteps", 0f) // Retrieve current step count
        loadData()
        saveData()

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        val purchasestotal = sharedPreferences.getFloat("Key2", 0f)


        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        if(savedNumber != 0f) {
            // Only update totalSteps if savedNumber is non-zero
            MainActivity.totalSteps = savedNumber
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        // Below added to capture the total steps.
        var tv_totalSteps1 = findViewById<TextView>(R.id.tv_totalSteps1)
        var chinastamp1 = findViewById<ImageButton>(R.id.chinastamp1)

        if (running && event != null) {
            val currentSteps = event.values[0]
            val stepsSinceLastUpdate = currentSteps - MainActivity.previousTotalSteps

            MainActivity.totalSteps += stepsSinceLastUpdate // Add steps since the last update to the total
            MainActivity.previousTotalSteps = currentSteps // Update previousTotalSteps for the next iteration
            // Convert totalSteps to an integer to remove decimal part
            val totalStepsInt = MainActivity.totalSteps.toInt()
            // Display the total steps in your TextView
            val totalStepsFormatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalStepsInt)
            tv_totalSteps1.text = totalStepsFormatted

            if (totalStepsInt >= 25000) chinastamp1.setVisibility(View.VISIBLE)
            else {chinastamp1.setVisibility(View.INVISIBLE)}


        }
    }
    override fun onResume() {
        super.onResume()
        running = true
        //Below variable is for the current date and time
        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }



    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", totalSteps)
        editor.putFloat("Key2", purchases)
        editor.apply()
    }
    override fun onDestroy() {
        super.onDestroy()
        saveData()

    }
    override fun onPause() {
        super.onPause()
        saveData()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not being utilized currently

    }

    fun backToMain(view: View) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("currentTotalSteps", currentTotalSteps) // Pass current step count back to MainActivity
        }
        startActivity(intent)
    }




}



