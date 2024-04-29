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
import java.time.LocalDateTime
import java.time.temporal.ChronoField;
import java.text.NumberFormat
import java.util.*
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

const val ShopScreenval = "com.example.ShopScreen"
const val page1screenval = "com.example.page1screen"
const val CHANNEL_ID = "your_channel_id"


class MainActivity : AppCompatActivity(), SensorEventListener {
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
        private val NOTIFICATION_ID = 1
        var ChinaView = false

    }

    // Creating a variable  which counts previous total
    // steps and it has also been given the value of 0 float


    // Attempting to utilizes the below as a variable that will gather points based off of purchases.
    private var purchases = 0f

    private val current = LocalDateTime.now()




    private var notificationDisplayed = false
    private var notificationDisplayed2 = false
    private var notificationDisplayed3 = false
    private var notificationDisplayed4 = false
    private var notificationDisplayed5 = false
    private var notificationDisplayed6 = false
    private var notificationDisplayed7 = false
    private var notificationDisplayed50test = false
    private var notificationDisplayed51test = false
    private var notificationDisplayed52test = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startStepCounterService()

        // Load the val ue of notificationDisplayed from SharedPreferences
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        notificationDisplayed = sharedPreferences.getBoolean("notificationDisplayed", false)
        notificationDisplayed2 = sharedPreferences.getBoolean("notificationDisplayed2", false)
        notificationDisplayed3 = sharedPreferences.getBoolean("notificationDisplayed3", false)
        notificationDisplayed4 = sharedPreferences.getBoolean("notificationDisplayed4", false)
        notificationDisplayed5 = sharedPreferences.getBoolean("notificationDisplayed5", false)
        notificationDisplayed6 = sharedPreferences.getBoolean("notificationDisplayed6", false)
        notificationDisplayed7 = sharedPreferences.getBoolean("notificationDisplayed7", false)
        notificationDisplayed50test = sharedPreferences.getBoolean("notificationDisplayed50test", false)
        notificationDisplayed51test = sharedPreferences.getBoolean("notificationDisplayed51test", false)
        notificationDisplayed52test = sharedPreferences.getBoolean("notificationDisplayed52test", false)
        ChinaView = sharedPreferences.getBoolean("ChinaView", false)

        checkPermission()

        if (intent.hasExtra("currentTotalSteps")) {
            totalSteps = intent.getFloatExtra("currentTotalSteps", 0f)
            previousTotalSteps = totalSteps // Update previous total steps
        }


        if (ChinaView) {
            // Show the buttons and text view if ChinaView is false
            findViewById<TextView>(R.id.ChinaButton).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.UnitedStatesButton).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.SKoreaButton).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.choosecountrytext).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.goaltext).visibility = View.VISIBLE
            findViewById<TextView>(R.id.ResumeButton).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_goalnumber).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_totalSteps).visibility = View.VISIBLE
            findViewById<TextView>(R.id.steps).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.imageView).visibility = View.VISIBLE
        }

        loadData()
        saveData()

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    private fun startStepCounterService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun sendNotification() {
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.hotpottes4t)
            .setContentTitle("Milestone achieved!")
            .setContentText("Come and see the new location you have reached!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that fires when the user taps the notification.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Notification will be removed when tapped

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define.
            notify(NOTIFICATION_ID, builder.build())
        }


        if (totalSteps >= 101000) { // Check if steps are >= 1000 and notification has not been sent
            notificationDisplayed = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed)
        }
        if (totalSteps >= 105100) {
            notificationDisplayed2 = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed2)
        }
        if (totalSteps >= 105204) {
            notificationDisplayed3 = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed3)
        }
        if (totalSteps >= 105230) {
            notificationDisplayed4 = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed4)
        }
        if (totalSteps >= 105380) {
            notificationDisplayed5 = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed5)
        }
        if (totalSteps >= 105400) {
            notificationDisplayed6 = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed6)
        }
        if (totalSteps >= 106300) {
            notificationDisplayed7 = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed7)
        }
        if (totalSteps >= 106370) {
            notificationDisplayed50test = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed50test)
        }
        if (totalSteps >= 116495) {
            notificationDisplayed51test = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed51test)
        }
        if (totalSteps >= 116585) {
            notificationDisplayed52test = true // Set the flag to true to indicate that notification has been displayed
            saveNotificationDisplayedState(notificationDisplayed52test)
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPermission() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it

            val ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE = 1001
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            // You can start accessing the physical activity data here
        }
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        val purchasestotal = sharedPreferences.getFloat("Key2", 0f)


        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        if (savedNumber != 0f) {
            // Only update totalSteps if savedNumber is non-zero //
            totalSteps = savedNumber
        }
    }
    private fun saveNotificationDisplayedState(displayed: Boolean) {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("notificationDisplayed", notificationDisplayed)
        editor.putBoolean("notificationDisplayed2", notificationDisplayed2)
        editor.putBoolean("notificationDisplayed3", notificationDisplayed3)
        editor.putBoolean("notificationDisplayed4", notificationDisplayed4)
        editor.putBoolean("notificationDisplayed5", notificationDisplayed5)
        editor.putBoolean("notificationDisplayed6", notificationDisplayed6)
        editor.putBoolean("notificationDisplayed7", notificationDisplayed7)
        editor.putBoolean("notificationDisplayed50test", notificationDisplayed50test)
        editor.putBoolean("notificationDisplayed51test", notificationDisplayed51test)
        editor.putBoolean("notificationDisplayed52test", notificationDisplayed52test)
        editor.apply()
    }


    override fun onSensorChanged(event: SensorEvent?) {
        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        // Below added to capture the total steps.
        var tv_totalSteps = findViewById<TextView>(R.id.tv_totalSteps)

        if (running && event != null) {
            val currentSteps = event.values[0]
            val stepsSinceLastUpdate = currentSteps - previousTotalSteps
            //Below variable is for the July badge
            var julybadge = findViewById<ImageButton>(R.id.july22badge)
            var questionbug = findViewById<ImageButton>(R.id.questionbug)


            totalSteps += stepsSinceLastUpdate // Add steps since the last update to the total
            previousTotalSteps = currentSteps // Update previousTotalSteps for the next iteration
            // Convert totalSteps to an integer to remove decimal part
            val totalStepsInt = totalSteps.toInt()
            // Display the total steps in your TextView
            val totalStepsFormatted =
                NumberFormat.getNumberInstance(Locale.getDefault()).format(totalStepsInt)
            tv_totalSteps.text = totalStepsFormatted

            if (totalStepsInt >= 5 && ChinaView) { // Check if steps are >= 1000 and notification has not been sent
                julybadge.setVisibility(View.VISIBLE)
                questionbug.setVisibility(View.INVISIBLE)
            } else if (ChinaView) {
                questionbug.setVisibility(View.VISIBLE)
            }
        }

        if (totalSteps >= 101000 && !notificationDisplayed) {


            sendNotification() // Call the function to send notification
            notificationDisplayed = true // Set the flag to true to indicate that notification has been displayed
        }

        if (totalSteps >= 105100 && !notificationDisplayed2) {


            sendNotification() // Call the function to send notification
            notificationDisplayed2 = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 105204 && !notificationDisplayed3) {


            sendNotification() // Call the function to send notification
            notificationDisplayed3 = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 105230 && !notificationDisplayed4) {


            sendNotification() // Call the function to send notification
            notificationDisplayed4 = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 105380 && !notificationDisplayed5) {


            sendNotification() // Call the function to send notification
            notificationDisplayed5 = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 105400 && !notificationDisplayed6) {


            sendNotification() // Call the function to send notification
            notificationDisplayed6 = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 106300 && !notificationDisplayed7) {


            sendNotification() // Call the function to send notification
            notificationDisplayed7 = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 106370 && !notificationDisplayed50test) {


            sendNotification() // Call the function to send notification
            notificationDisplayed50test = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 116495 && !notificationDisplayed51test) {


            sendNotification() // Call the function to send notification
            notificationDisplayed51test = true // Set the flag to true to indicate that notification has been displayed
        }
        if (totalSteps >= 116585 && !notificationDisplayed52test) {


            sendNotification() // Call the function to send notification
            notificationDisplayed52test = true // Set the flag to true to indicate that notification has been displayed
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
        editor.putBoolean("ChinaView", ChinaView)
        editor.apply()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not being utilized currently

    }



    fun july22badgescreen(view: View){
        var ResetButton = findViewById<TextView>(R.id.ResetButton)
        val message = ResetButton.text.toString()
        val intent = Intent(this, page1screen::class.java).apply {
            putExtra(page1screenval, message)
            putExtra("previousTotalSteps", previousTotalSteps)
        }
        startActivity(intent)
    }

    fun resumebutton(view: View) {
        val intent = when {
            totalSteps > 5000000 -> Intent(this, page3screen::class.java)
            totalSteps > 30000 -> Intent(this, page3screen::class.java)
            totalSteps > 25000 -> Intent(this, page2screen::class.java)
            totalSteps > 5 -> Intent(this, page1screen::class.java)
            else -> null
        }

        if (intent != null) {
            startActivity(intent)
        } else {
            // Handle the case when total steps are not in any defined range
            Toast.makeText(this, "Total steps are not in any defined range", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        startService(Intent(this, StepCounterService::class.java))
    }

// Don't forget to stop the service when the app is closed

    override fun onDestroy() {
        super.onDestroy()
        saveData()

    }

    fun toChina(view: View) {
        var ChinaButton = findViewById<TextView>(R.id.ChinaButton)
        var USButton = findViewById<TextView>(R.id.UnitedStatesButton)
        var SkoreaButton = findViewById<TextView>(R.id.SKoreaButton)
        var choosecountrytext = findViewById<TextView>(R.id.choosecountrytext)
        var goaltext = findViewById<TextView>(R.id.goaltext)
        var ResumeButton = findViewById<TextView>(R.id.ResumeButton)
        var tv_goalnumber = findViewById<TextView>(R.id.tv_goalnumber)
        var tv_totalSteps = findViewById<TextView>(R.id.tv_totalSteps)
        var steps = findViewById<TextView>(R.id.steps)
        var ChinaText = findViewById<TextView>(R.id.ChinaCountryText)
        var julybadge = findViewById<ImageButton>(R.id.july22badge)
        var questionbug = findViewById<ImageButton>(R.id.questionbug)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning")
        builder.setMessage("Are you sure you want to proceed to China? This will be a long journey and you must complete it before choosing another country!")
        builder.setPositiveButton("Lets do it") { dialogInterface: DialogInterface, i: Int ->

            ChinaView = true
            ChinaButton.setVisibility(View.INVISIBLE)
            USButton.setVisibility(View.INVISIBLE)
            SkoreaButton.setVisibility(View.INVISIBLE)
            choosecountrytext.setVisibility(View.INVISIBLE)
            goaltext.setVisibility(View.VISIBLE)
            ResumeButton.setVisibility(View.VISIBLE)
            tv_goalnumber.setVisibility(View.VISIBLE)
            ChinaText.setVisibility(View.VISIBLE)
            tv_totalSteps.setVisibility(View.VISIBLE)
            steps.setVisibility(View.VISIBLE)
            imageView.setVisibility(View.VISIBLE)
            if (totalSteps >= 5) { // Check if steps are >= 1000 and notification has not been sent
                julybadge.setVisibility(View.VISIBLE)
            } else {
                questionbug.setVisibility(View.VISIBLE)
            }


            saveData()

        }
        builder.setNegativeButton("Let me think about it...") { dialogInterface: DialogInterface, i: Int ->
            // Do nothing or add any cancellation logic here
        }
        builder.show()
    }

    fun toSouthKorea(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning")
        builder.setMessage("Are you sure you want to proceed to South Korea? This will be a long journey and you must complete it before choosing another country!")
        builder.setPositiveButton("Lets do it") { dialogInterface: DialogInterface, i: Int ->

        }
        builder.setNegativeButton("Let me think about it...") { dialogInterface: DialogInterface, i: Int ->
            // Do nothing or add any cancellation logic here
        }
        builder.show()
    }
    fun toUS(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning")
        builder.setMessage("Are you sure you want to proceed to the United States? This will be a long journey and you must complete it before choosing another country!")
        builder.setPositiveButton("Lets do it") { dialogInterface: DialogInterface, i: Int ->
            // Add your code to proceed to China
        }
        builder.setNegativeButton("Let me think about it...") { dialogInterface: DialogInterface, i: Int ->
            // Do nothing or add any cancellation logic here
        }
        builder.show()
    }



}