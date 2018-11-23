package nl.ecci.hamers.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object PermissionUtils {
    private const val PERMISSION_REQUEST_CODE = 1043

    fun checkLocationPermission(activity: Activity?): Boolean {
        if (activity == null) {
            return false
        }
        return if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE)
            false
        } else {
            true
        }
    }

}