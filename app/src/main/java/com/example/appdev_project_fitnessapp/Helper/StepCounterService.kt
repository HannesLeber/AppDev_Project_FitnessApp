package com.example.appdev_project_fitnessapp.Helper

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.app.notifications.NotificationHelper
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.Repo.StepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    
    private lateinit var repository: StepRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    
    private lateinit var prefs: SharedPreferences
    
    // Hilfsvariablen für die Berechnung
    private var stepsAtStartOfDay = 0
    private var totalStepsAlreadySaved = 0
    
    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        val database = AppDatabase.getDatabase(this)
        repository = StepRepository(database.dailyStepDao())
        
        prefs = getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)
        
        // Letzten bekannten Stand laden
        stepsAtStartOfDay = prefs.getInt("steps_at_start_of_day", -1)
        
        registerSensor()
        startForeground(NOTIFICATION_ID, createNotification(0))
    }

    private fun registerSensor() {
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentSensorValue = event.values[0].toInt()
            
            // Initialisierung bei erstem Start oder neuem Tag
            if (stepsAtStartOfDay == -1 || isNewDay()) {
                stepsAtStartOfDay = currentSensorValue
                saveStepsAtStartOfDay(stepsAtStartOfDay)
                resetDailyStepsInDb()
            }
            
            // Reboot Handling: Wenn der Sensorwert kleiner ist als der Startwert
            if (currentSensorValue < stepsAtStartOfDay) {
                stepsAtStartOfDay = 0 // Sensor wurde zurückgesetzt
                saveStepsAtStartOfDay(0)
            }

            val stepsToday = currentSensorValue - stepsAtStartOfDay
            updateSteps(stepsToday)
        }
    }

    private fun isNewDay(): Boolean {
        val lastUpdateDate = prefs.getLong("last_update_date", 0)
        val lastCalendar = Calendar.getInstance().apply { timeInMillis = lastUpdateDate }
        val nowCalendar = Calendar.getInstance()
        
        return lastCalendar.get(Calendar.DAY_OF_YEAR) != nowCalendar.get(Calendar.DAY_OF_YEAR) ||
               lastCalendar.get(Calendar.YEAR) != nowCalendar.get(Calendar.YEAR)
    }

    private fun updateSteps(steps: Int) {
        serviceScope.launch {
            repository.updateSteps(steps, 10000) // 10000 ist Default Ziel
            updateNotification(steps)
            
            prefs.edit().putLong("last_update_date", System.currentTimeMillis()).apply()
        }
    }

    private fun resetDailyStepsInDb() {
        serviceScope.launch {
            repository.updateSteps(0, 10000)
        }
    }

    private fun saveStepsAtStartOfDay(value: Int) {
        prefs.edit().putInt("steps_at_start_of_day", value).apply()
    }

    private fun createNotification(steps: Int): Notification {
        return NotificationCompat.Builder(this, NotificationHelper.STEP_CHANNEL_ID)
            .setContentTitle("Schrittzähler aktiv")
            .setContentText("Heute bereits $steps Schritte geschafft!")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(steps: Int) {
        val notification = createNotification(steps)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
