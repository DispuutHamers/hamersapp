package nl.ecci.hamers.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_sticker.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.Sticker
import nl.ecci.hamers.utils.PermissionUtils
import nl.ecci.hamers.utils.Utils
import org.jetbrains.anko.support.v4.act
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class StickerFragment : HamersFragment(),
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private val dataSet = ArrayList<Sticker>()
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mapView: MapView? = null
    private var mMap: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrLocationMarker: Marker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sticker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the MapView from the XML layout and creates it
        mapView = stickers_map as MapView
        mapView?.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.checkLocationPermission(activity)
        }

        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        hamers_fab.setOnClickListener {
            postLocationDialog()
        }

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap?.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap?.isMyLocationEnabled = true
        }
        addMarkers()

        mMap?.setOnMarkerClickListener(this)
        mMap?.setOnMarkerDragListener(this)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.showInfoWindow()
        return false
    }

    private fun onRefresh() {
        Loader.getData(act, Loader.STICKERURL, -1, object : GetCallback {
            override fun onSuccess(response: String) {
                populateMap().execute(dataSet)
            }
        }, null)
    }

    private fun addMarkers() {
        for (sticker in dataSet) {
            mMap?.addMarker(MarkerOptions()
                    .position(LatLng(sticker.lat.toDouble(), sticker.lon.toDouble()))
                    .title(sticker.notes))
        }
    }

    override fun onResume() {
        mapView?.onResume()
        super.onResume()
        onRefresh()
        activity?.title = resources.getString(R.string.navigation_item_stickers)

        val params = hamers_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = stickers_map.id
        hamers_fab.layoutParams = params
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    private fun postLocationDialog() {
        val alert = AlertDialog.Builder(activity)
                .setTitle(R.string.sticker_post_title)
                .setMessage(R.string.sticker_post_message)

        val input = EditText(activity)
        input.setSingleLine()
        input.setHint(R.string.sticker_post_note)

        val container = FrameLayout(activity)
        container.addView(input)

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        input.layoutParams = params

        alert.setView(container)

        alert.setPositiveButton(android.R.string.yes) { _, _ ->
            postSticker(input.text.toString())
        }
        alert.setNegativeButton(android.R.string.no) { _, _ ->
            // Do nothing.
        }

        // Only show dialog if permissions are given
        if (PermissionUtils.checkLocationPermission(activity)) {
            alert.show()
        } else {
            Toast.makeText(activity, R.string.sticker_no_permission, Toast.LENGTH_LONG).show()
        }
    }

    private fun postSticker(notes: String) {
        val body = JSONObject()
        val lat = mCurrLocationMarker?.position?.latitude
        val lon = mCurrLocationMarker?.position?.longitude
        if (lat != null && lon != null) {
            try {
                body.put("lat", lat.toString())
                body.put("lon", lon.toString())
                body.put("notes", notes)
                Loader.postOrPatchData(act, Loader.STICKERURL, body, Utils.notFound, null)
            } catch (ignored: JSONException) {
            }
        } else {
            Toast.makeText(activity, R.string.generic_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(act)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient?.connect()
    }

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)

        if (mCurrLocationMarker == null) {
            val markerOptions = MarkerOptions()
            markerOptions.draggable(true)
            markerOptions.position(latLng)
            markerOptions.title("Je locatie")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker())
            mCurrLocationMarker = mMap?.addMarker(markerOptions)
        }

        // Move mMap camera
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(11F))

        // Stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 1000
        mLocationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(act,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        // Nothing
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        // Nothing
    }

    override fun onMarkerDragStart(marker: Marker) {
        // Nothing
    }

    override fun onMarkerDrag(p0: Marker) {
        // Nothing
    }

    override fun onMarkerDragEnd(marker: Marker) {
        mCurrLocationMarker?.position = marker.position
    }

    private inner class populateMap : AsyncTask<ArrayList<Sticker>, Void, ArrayList<Sticker>>() {
        @SafeVarargs
        override fun doInBackground(vararg param: ArrayList<Sticker>): ArrayList<Sticker> {
            val gson = GsonBuilder().create()
            val type = object : TypeToken<ArrayList<Sticker>>() {

            }.type

            return gson.fromJson<ArrayList<Sticker>>(prefs?.getString(Loader.STICKERURL, null), type)
        }

        override fun onPostExecute(result: ArrayList<Sticker>) {
            if (!result.isEmpty()) {
                dataSet.clear()
                dataSet.addAll(result)
                addMarkers()
            }
        }
    }
}
