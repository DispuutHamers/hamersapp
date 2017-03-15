package nl.ecci.hamers.stickers

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import com.android.volley.VolleyError
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_sticker.*
import nl.ecci.hamers.MainActivity.prefs
import nl.ecci.hamers.R
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class StickerFragment : Fragment(), OnMapReadyCallback {

    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private val dataSet = ArrayList<Sticker>()
    private var locationManager: LocationManager? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_sticker, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the MapView from the XML layout and creates it
        mapView = stickers_map as MapView
        mapView?.onCreate(savedInstanceState)

        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val params = hamers_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = stickers_map.id
        hamers_fab.layoutParams = params
        hamers_fab.setOnClickListener {
            postLocationDialog()
        }

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isMyLocationButtonEnabled = false
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map!!.isMyLocationEnabled = true
        }
        addMarkers()
    }

    private fun onRefresh() {
        Loader.getData(context, Loader.STICKERURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateMap().execute(dataSet)
            }

            override fun onError(error: VolleyError) {
                // Nothing
            }
        }, null)
    }

    private fun addMarkers() {
        if (map != null && !dataSet.isEmpty()) {
            for (sticker in dataSet) {
                map!!.addMarker(MarkerOptions().position(LatLng(sticker.lat.toDouble(), sticker.lon.toDouble())))
            }
        }
    }

    override fun onResume() {
        mapView!!.onResume()
        super.onResume()
        onRefresh()
        activity.title = resources.getString(R.string.navigation_item_stickers)
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
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
            postSticker()
        }
        alert.setNegativeButton(android.R.string.no) { _, _ ->
            // Do nothing.
        }

        alert.show()
    }

    private fun postSticker() {
        val lastKnownLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val body = JSONObject()
        try {
            body.put("lat", lastKnownLocation?.latitude)
            body.put("lon", lastKnownLocation?.longitude)
        } catch (ignored: JSONException) {
        }

        Loader.postOrPatchData(activity, Loader.STICKERURL, body, -1, null)
    }

    private inner class populateMap : AsyncTask<ArrayList<Sticker>, Void, ArrayList<Sticker>>() {
        @SafeVarargs
        override fun doInBackground(vararg param: ArrayList<Sticker>): ArrayList<Sticker> {
            val gson = GsonBuilder().create()
            val type = object : TypeToken<ArrayList<Sticker>>() {

            }.type

            return gson.fromJson<ArrayList<Sticker>>(prefs.getString(Loader.STICKERURL, null), type)
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
