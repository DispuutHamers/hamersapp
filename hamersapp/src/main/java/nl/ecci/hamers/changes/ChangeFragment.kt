package nl.ecci.hamers.changes

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.hamers_list_fragment.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.MainActivity.prefs
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.HamersListFragment
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader
import java.util.*

class ChangeFragment : HamersListFragment() {

    private val dataSet = ArrayList<Change>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.hamers_list_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        hamers_recyclerview.adapter = ChangeAdapter(dataSet, activity)
        hamers_fab.visibility = View.GONE

        populateList().execute()
        onRefresh()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(context, Loader.CHANGEURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }

            override fun onError(error: VolleyError) {
                // Nothing
            }
        }, null)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity.title = resources.getString(R.string.navigation_item_changes)
    }

    override fun onDestroy() {
        super.onDestroy()
        AnimateFirstDisplayListener.displayedImages.clear()
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Change>>() {
        override fun doInBackground(vararg params: String): ArrayList<Change> {
            val result: ArrayList<Change>
            val type = object : TypeToken<ArrayList<Change>>() {
            }.type
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            if (params.isNotEmpty()) {
                result = gson.fromJson<ArrayList<Change>>(params[0], type)
            } else {
                result = gson.fromJson<ArrayList<Change>>(prefs.getString(Loader.BEERURL, null), type)

            }
            return result
        }

        override fun onPostExecute(result: ArrayList<Change>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                notifyAdapter()
            }
            setRefreshing(false)
        }
    }
}