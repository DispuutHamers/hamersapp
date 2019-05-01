package nl.ecci.hamers.ui.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.Change
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.adapters.ChangeAdapter
import nl.ecci.hamers.utils.DividerItemDecoration
import org.jetbrains.anko.padding
import java.util.*

class ChangeFragment : HamersListFragment() {

    private val dataSet = ArrayList<Change>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hamers_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            hamers_list.addItemDecoration(DividerItemDecoration(it))
            hamers_list.adapter = ChangeAdapter(it, dataSet)
        }
        hamers_list.padding = 0
        hamers_fab.visibility = View.GONE

        populateList().execute()
        // Refresh only at create (not on resume!)
        onRefresh()
    }

    override fun onRefresh() {
        setRefreshing(true)
        // Load changes
        Loader.getData(requireContext(), Loader.CHANGEURL, -1, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
        // Load all data (to prevent clicking on items that do not (yet) exist
        Loader.getAllData(requireContext())
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.navigation_item_changes)
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Change>>() {
        override fun doInBackground(vararg params: String): ArrayList<Change> {
            val result = ArrayList<Change>()
            var tempList: ArrayList<Change>? = null
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()
            val type = object : TypeToken<ArrayList<Change>>() {
            }.type

            if (params.isNotEmpty()) {
                tempList = gson.fromJson<ArrayList<Change>>(params[0], type)
            } else {
                val changes: String? = prefs?.getString(Loader.CHANGEURL, null)
                if (changes != null) {
                    tempList = gson.fromJson<ArrayList<Change>>(changes, type)
                }
            }

            // Filter out changes regarding device ID's and destroys of sign ups
            tempList?.filterTo(result) {
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