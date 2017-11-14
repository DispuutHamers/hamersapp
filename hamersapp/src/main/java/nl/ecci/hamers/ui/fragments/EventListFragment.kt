package nl.ecci.hamers.ui.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.Event
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.NewEventActivity
import nl.ecci.hamers.ui.adapters.EventFragmentAdapter
import nl.ecci.hamers.ui.adapters.EventListAdapter
import org.jetbrains.anko.support.v4.act
import java.util.*
import kotlin.collections.ArrayList

class EventListFragment : HamersListFragment() {

    private val dataSet = ArrayList<Event>()
    private var upcoming: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        upcoming = arguments!!.getBoolean(EventFragmentAdapter.upcoming, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hamers_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.adapter = EventListAdapter(act, dataSet)

        hamers_fab.setOnClickListener {
            startActivityForResult(Intent(activity, NewEventActivity::class.java), -1)
        }

        // Disable the scrollbar, since this list is very short and the scrollbar behaves weirdly
        if (upcoming) {
            hamers_list.isVerticalScrollBarEnabled = false
        }

        populateList().execute()
    }

    override fun onRefresh() {
        setRefreshing(true)
        if (upcoming) {
            val params = HashMap<String, String>()
            params.put("sorted", "asc-desc")
            params.put("future", "true")
            Loader.getData(act, Loader.EVENTURL, -1, object : GetCallback {
                override fun onSuccess(response: String) {
                    populateList().execute(response)
                }
            }, params)
        } else {
            Loader.getData(act, Loader.EVENTURL, -1, object : GetCallback {
                override fun onSuccess(response: String) {
                    // Only save normal event list
                    prefs?.edit()?.putString(Loader.EVENTURL, response)?.apply()
                    populateList().execute(response)
                }
            }, null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.scroll_top -> {
                hamers_list.smoothScrollToPosition(0)
                true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.news_event_menu, menu)
        val menuItem = menu.findItem(R.id.event_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                (hamers_list.adapter as EventListAdapter).filter.filter(s.toLowerCase())
                return false
            }
        })
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Event>?>() {

        override fun doInBackground(vararg params: String): ArrayList<Event>? {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()
            val type = object : TypeToken<ArrayList<Event>>() {
            }.type

            if (params.isNotEmpty()) {
                return gson.fromJson<ArrayList<Event>>(params[0], type)
            } else if (!upcoming) {
                return gson.fromJson<ArrayList<Event>>(prefs?.getString(Loader.EVENTURL, null), type)
            }
            return null
        }

        override fun onPostExecute(result: ArrayList<Event>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                if (!upcoming)
                    Collections.reverse(dataSet)
                notifyAdapter()
                setRefreshing(false)
            }
        }
    }
}

