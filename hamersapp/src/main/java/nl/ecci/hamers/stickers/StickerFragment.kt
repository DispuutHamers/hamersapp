package nl.ecci.hamers.stickers

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.volley.VolleyError
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.util.ArrayList

import nl.ecci.hamers.R
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader

import nl.ecci.hamers.MainActivity.prefs

class StickerFragment : Fragment(), OnMapReadyCallback {

    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private val dataSet = ArrayList<Sticker>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.sticker_fragment, container, false)

        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById(R.id.stickers_map) as MapView
        mapView!!.onCreate(savedInstanceState)

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView!!.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isMyLocationButtonEnabled = false
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
