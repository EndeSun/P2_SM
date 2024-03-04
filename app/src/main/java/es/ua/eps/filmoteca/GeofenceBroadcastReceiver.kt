package es.ua.eps.filmoteca
import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.e(TAG, "Error receiving geofence event: ${geofencingEvent.errorCode}")
                return
            }
        }

        val geofenceList = geofencingEvent?.triggeringGeofences
        if (geofenceList != null) {
            for (geofence in geofenceList) {
                Log.d(TAG, "Triggered geofence ID: ${geofence.requestId}")
            }
        }

        val transitionType = geofencingEvent?.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d(TAG, "Entered geofence")
                Toast.makeText(context, "Entered geofence", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "Entered geofence", "",
                    MapActivity::class.java
                )
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Log.d(TAG, "Dwelling in geofence")
                Toast.makeText(context, "Dwelling in geofence", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "Dwelling in geofence", "",
                    MapActivity::class.java
                )
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d(TAG, "Exited geofence")
                Toast.makeText(context, "Exited geofence", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "Exited geofence", "",
                    MapActivity::class.java
                )
            }
            else -> {
                Log.e(TAG, "Unknown transition type: $transitionType")
                Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val TAG = "GeofenceBroadcastReceiver"
        const val ACTION_GEOFENCE_EVENT = "es.ua.eps.filmoteca.ACCION_GEOFENCE_EVENT"
    }
}