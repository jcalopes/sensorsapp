package pt.ipp.estg.sensorsapp.src

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import android.provider.Settings
import pt.ipp.estg.sensorsapp.BuildConfig
import pt.ipp.estg.sensorsapp.databinding.ActivityLocationBinding
import pt.ipp.estg.sensorsapp.src.Services.BackgroundTrackActivity
import pt.ipp.estg.sensorsapp.src.utils.hasPermission
import pt.ipp.estg.sensorsapp.src.utils.requestPermissionWithRationale

class LocationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLocationBinding
    var locationService:BackgroundTrackActivity? = null
    val broadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(baseContext,"Service Finished",Toast.LENGTH_SHORT).show()
        }
    }

    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(binding.container,
            "The fine location permission is needed for core functionality.",
            Snackbar.LENGTH_LONG
        ).setAction("Ok") {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE)
        }
    }

    companion object{
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceConnection by lazy{
            object: ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    locationService = (service as BackgroundTrackActivity.MyBinder).getService()
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    locationService = null
                }
            }
        }

        val i = Intent(this,BackgroundTrackActivity::class.java)

        binding.btnStart.setOnClickListener {
            if(locationRequestAccepted()) {
                startService(i)
            }else{
                requestPermissionWithRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                    fineLocationRationalSnackbar
                )
            }
        }

        binding.btnStop.setOnClickListener {
            val intent = Intent("pt.ipp.estg.sensorapp.src.MainActivity");
            sendBroadcast(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("asd", "onRequestPermissionResult()")

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    Log.d("asd", "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    Snackbar.make(
                        binding.container, "You approved FINE location, carry (and click) on!",
                        Snackbar.LENGTH_LONG).show()
                else -> {
                    Snackbar.make(
                        binding.container,
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

    fun locationRequestAccepted():Boolean {
        val permissionApproved =
            applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        return (permissionApproved)
    }


    override fun onResume() {
        super.onResume()
        val intentFil= IntentFilter("pt.ipp.estg.sensorapp.src.BackgroundDetectActivities");
        registerReceiver(broadcastReceiver,intentFil);
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

}