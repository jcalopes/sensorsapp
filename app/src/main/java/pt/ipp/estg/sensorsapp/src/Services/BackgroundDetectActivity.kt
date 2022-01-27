package pt.ipp.estg.sensorsapp.src.Services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

//Transition API - Google Play Services
class BackgroundDetectActivity:BroadcastReceiver() {
    lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context!!

        Log.d("asd", "onReceive")

        if (ActivityTransitionResult.hasResult(intent)) {
            var result = ActivityTransitionResult.extractResult(intent)

            if (result != null) {
                processTransitionResult(result)
            }
        }
    }

    fun processTransitionResult(result: ActivityTransitionResult) {
        Log.d("asd", "onReceive")
        for (event in result.transitionEvents) {
            when (event.activityType) {
                DetectedActivity.ON_BICYCLE,
                DetectedActivity.IN_VEHICLE-> {
                    Log.d("asd", "Detected In car")
                }
                DetectedActivity.RUNNING-> {
                    Log.d("asd", "Detected running")
                }
                DetectedActivity.WALKING -> {
                    Log.d("asd", "Detected Walking")
                }
            }
        }
    }

}