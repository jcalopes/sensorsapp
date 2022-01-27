package pt.ipp.estg.sensorsapp.src.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import pt.ipp.estg.sensorsapp.src.Services.BackgroundDetectActivity
import java.util.ArrayList

class TransitionRecognitionAPI {
        lateinit var mContext: Context
        lateinit var mPendingIntent: PendingIntent

        fun startTracking(context: Context) {
            mContext = context
            launchTransitionsTracker()
            Log.d("asd", "startTracking")
        }

        fun stopTracking() {
            if (mContext != null && mPendingIntent != null) {
                ActivityRecognition.getClient(mContext).removeActivityTransitionUpdates(mPendingIntent)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        mPendingIntent.cancel()
                    })
                    .addOnFailureListener(OnFailureListener { e -> Log.e("asd", "Transitions could not be unregistered: $e") })
            }
        }

        private fun launchTransitionsTracker() {
            Log.d("asd", "launchTracker")

            val transitions = ArrayList<ActivityTransition>()

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())

            transitions.add(
                ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())


            val request = ActivityTransitionRequest(transitions)
            val activityRecognitionClient = ActivityRecognition.getClient(mContext)

            val intent = Intent(mContext, BackgroundDetectActivity::class.java)
            mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)

            val task = activityRecognitionClient.requestActivityTransitionUpdates(request, mPendingIntent)
            task.addOnSuccessListener(
                object : OnSuccessListener<Void> {
                    override fun onSuccess(p0: Void?) {
                        Log.d("asd","Entrou activity recognition" )
                    }
                })

            task.addOnFailureListener(
                OnFailureListener { })
        }
}