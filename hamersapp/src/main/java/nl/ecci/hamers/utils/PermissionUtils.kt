package nl.ecci.hamers.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


object PermissionUtils {
    private val PERMISSION_REQUEST_CODE = 1043

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