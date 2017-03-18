package nl.ecci.hamers.events

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.*
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.MainActivity.prefs
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.HamersListFragment
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader
import java.util.*

class EventListFragment : HamersListFragment() {

    private val dataSet = ArrayList<Event>()
    private var upcoming: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        upcoming = arguments.getBoolean(EventFragmentAdapter.upcoming, false)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_hamers_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_recyclerview.adapter = EventListAdapter(activity, dataSet)

        hamers_fab.setOnClickListener {
            startActivityForResult(Intent(activity, NewEventActivity::class.java), -1)
        }

        // Disable the scrollbar, since this list is very short and the scrollbar behaves weirdly
        if (upcoming) {
            hamers_recyclerview.isVerticalScrollBarEnabled = false
        }

        populateList().execute()
        onRefresh()
    }

    override fun onRefresh() {
        setRefreshing(true)
        val params = HashMap<String, String>()
        if (upcoming) {
            params.put("sorted", "date-desc")
            params.put("future", "true")
            Loader.getData(context, Loader.EVENTURL, object : GetCallback {
                override fun onSuccess(response: String) {
                    populateList().execute(response)
                }

                override fun onError(error: VolleyError) {
                    // Nothing
                }
            }, params)
        } else {
            params.put("sorted", "date-asc")
            Loader.getData(context, Loader.EVENTURL, object : GetCallback {
                override fun onSuccess(response: String) {
                    // Only save normal event list
                    prefs.edit().putString(Loader.EVENTURL, response).apply()
                    populateList().execute(response)
                }

                override fun onError(error: VolleyError) {
                    // Nothing
                }
            }, params)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.scroll_top -> {
                hamers_recyclerview.smoothScrollToPosition(0)
                return true
            }
            else -> return false
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
                (hamers_recyclerview.adapter as EventListAdapter).filter.filter(s.toLowerCase())
                return false
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onRefresh()
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
            } else {
                val tempList = gson.fromJson<ArrayList<Event>>(prefs.getString(Loader.EVENTURL, null), type)
                if (upcoming) {
                    // Only 'future' elements
                    val result = ArrayList<Event>()
                    val now = Date()

                    tempList?.filterTo(result) { now.before(it.date) }
                    Collections.reverse(result)
                    return result
                } else {
                    return tempList
                }
            }
        }

        override fun onPostExecute(result: ArrayList<Event>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                Collections.reverse(dataSet)
                notifyAdapter()
            }
            setRefreshing(false)
        }
    }
}

