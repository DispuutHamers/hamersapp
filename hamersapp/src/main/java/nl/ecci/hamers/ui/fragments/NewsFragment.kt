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
import nl.ecci.hamers.models.News
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.NewNewsActivity
import nl.ecci.hamers.ui.adapters.NewsAdapter
import java.util.*

class NewsFragment : HamersListFragment() {

    private val dataSet = ArrayList<News>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_hamers_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.adapter = NewsAdapter(dataSet)

        hamers_fab.setOnClickListener { startActivityForResult(Intent(activity, NewNewsActivity::class.java), 1) }

        onRefresh()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.scroll_top -> {
                hamers_list!!.smoothScrollToPosition(0)
                return true
            }
            else -> return false
        }
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(context, Loader.NEWSURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity.title = resources.getString(R.string.navigation_item_news)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.news_event_menu, menu)
        val menuItem = menu!!.findItem(R.id.event_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                (hamers_list.adapter as NewsAdapter).filter.filter(s.toLowerCase())
                return false
            }
        })
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<News>>() {
        override fun doInBackground(vararg params: String): ArrayList<News> {
            val result: ArrayList<News>
            val type = object : TypeToken<ArrayList<News>>() {
            }.type
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            if (params.isNotEmpty()) {
                result = gson.fromJson<ArrayList<News>>(params[0], type)
            } else {
                result = gson.fromJson<ArrayList<News>>(prefs?.getString(Loader.NEWSURL, null), type)
            }
            return result
        }

        override fun onPostExecute(result: ArrayList<News>?) {
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
