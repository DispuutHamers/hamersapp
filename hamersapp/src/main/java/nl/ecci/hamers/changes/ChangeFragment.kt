package nl.ecci.hamers.changes

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.MainActivity.prefs
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.DividerItemDecoration
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
        return inflater?.inflate(R.layout.fragment_hamers_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.addItemDecoration(DividerItemDecoration(activity))
        hamers_list.adapter = ChangeAdapter(activity, dataSet)
        hamers_fab.visibility = View.GONE

        populateList().execute()
        onRefresh()
    }

    override fun onRefresh() {
        setRefreshing(true)
        // Load changes
        Loader.getData(context, Loader.CHANGEURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
        // Load all data (to prevent clicking on items that do not (yet) exist
        Loader.getAllData(activity)
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
            val result = ArrayList<Change>()
            val tempList: ArrayList<Change>
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()
            val type = object : TypeToken<ArrayList<Change>>() {
            }.type

            if (params.isNotEmpty()) {
                tempList = gson.fromJson<ArrayList<Change>>(params[0], type)
            } else {
                tempList = gson.fromJson<ArrayList<Change>>(prefs.getString(Loader.CHANGEURL, null), type)
            }

            // Filter out changes regarding device ID's and destroys of sign ups
            tempList.filterTo(result) {
                it.itemType != Change.ItemType.DEVICE && !(it.itemType == Change.ItemType.SIGNUP && it.event == Change.Event.DESTROY)
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