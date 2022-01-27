package pt.ipp.estg.sensorsapp.src

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import pt.ipp.estg.sensorsapp.BuildConfig
import pt.ipp.estg.sensorsapp.databinding.ActivityMainBinding
import pt.ipp.estg.sensorsapp.src.utils.TransitionRecognitionAPI
import pt.ipp.estg.sensorsapp.src.utils.requestPermissionWithRationale

class MainActivity : AppCompatActivity(),SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mTransitionRecognition: TransitionRecognitionAPI
    private var sensorManager:SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousSteps = 0f

    private val recognitionLocationRationalSnackbar by lazy {
        Snackbar.make(binding.mainContainer,
            "The fine location permission is needed for core functionality.",
            Snackbar.LENGTH_LONG
        ).setAction("Ok") {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACTIVITY_RECOGNITION_PERMISSIONS_REQUEST_CODE
            )
        }
    }
    companion object{
        private const val REQUEST_ACTIVITY_RECOGNITION_PERMISSIONS_REQUEST_CODE = 36
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        binding.circularProgressBar.apply {
            progressMax = 3000f
        }

        binding.btnLocationPage.setOnClickListener {
            var i = Intent(baseContext, LocationActivity::class.java)
            startActivity(i)
        }

        initTransitionRecognition()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
        //mTransitionRecognition.stopTracking()
    }

    override fun onResume() {
        super.onResume()
        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if(stepSensor == null){
            Toast.makeText(this,"No sensor detected on this device.",Toast.LENGTH_LONG).show()
        }else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousSteps.toInt()

            binding.txtFootSteps.text = ("$currentSteps")
            binding.circularProgressBar.apply {
                setProgressWithAnimation(currentSteps.toFloat())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       //Not necessary
    }

    fun initTransitionRecognition(){
        mTransitionRecognition = TransitionRecognitionAPI()
        if (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWithRationale(
                Manifest.permission.ACTIVITY_RECOGNITION,
                REQUEST_ACTIVITY_RECOGNITION_PERMISSIONS_REQUEST_CODE,
                recognitionLocationRationalSnackbar)
        }else{
            mTransitionRecognition.startTracking(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("asd", "onRequestPermissionResult()")

        if (requestCode == REQUEST_ACTIVITY_RECOGNITION_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    Log.d("asd", "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    Snackbar.make(
                        binding.mainContainer, "You are automatically recording activity !",
                        Snackbar.LENGTH_LONG).show()
                else -> {
                    Snackbar.make(
                        binding.mainContainer,
                        "Fine location permission was denied but is needed for core functionality.",
                        Snackbar.LENGTH_LONG)
                        .setAction("Definições") {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1",0f)

        previousSteps = savedNumber
    }

    private fun saveData(){
        val sharedPreferences = getSharedPreferences("myPrefs",Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1",previousSteps)
        editor.apply()
    }

    private fun resetSteps(){
        binding.txtFootSteps.setOnClickListener {
            previousSteps = totalSteps
            binding.txtFootSteps.text = "0"
            saveData()
        }
    }

}