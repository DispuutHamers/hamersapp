package nl.ecci.hamers.quotes

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.hamers_fragment.*
import kotlinx.android.synthetic.main.hamers_fragment.view.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.MainActivity.prefs
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DividerItemDecoration
import nl.ecci.hamers.helpers.HamersFragment
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader
import java.util.*


class QuoteFragment : HamersFragment(), DialogInterface.OnDismissListener {

    private val dataSet = ArrayList<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.hamers_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSwiper(hamers_recyclerview, LinearLayoutManager(activity), hamers_swipe_container)

        hamers_recyclerview.addItemDecoration(DividerItemDecoration(activity))
        hamers_recyclerview.adapter = QuoteAdapter(dataSet, context)

        // When user presses "+" in QuoteListFragment, start new dialog with NewQuoteFragment
        hamers_fab.setOnClickListener {
            NewQuoteFragment().show(childFragmentManager, "quotes")
        }

        populateList().execute()
        onRefresh()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(context, Loader.QUOTEURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }

            override fun onError(error: VolleyError) {
                // Nothing
            }
        }, null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quote_menu, menu)
        val menuItem = menu.findItem(R.id.quote_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                (hamers_recyclerview.adapter as QuoteAdapter).filter.filter(s.toLowerCase())
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity.title = resources.getString(R.string.navigation_item_quotes)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scroll_top -> {
                scrollTop()
                return true
            }
            else -> return false
        }
    }

    private fun scrollTop() {
        view?.hamers_recyclerview?.smoothScrollToPosition(0)
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        onRefresh()
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Quote>>() {

        override fun doInBackground(vararg params: String): ArrayList<Quote> {
            val result: ArrayList<Quote> = ArrayList()
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()
            val type = object : TypeToken<ArrayList<Quote>>() {

            }.type

            val quotes = prefs.getString(Loader.QUOTEURL, null)

            if (params.isNotEmpty()) {
                result.addAll(gson.fromJson<ArrayList<Quote>>(params[0], type))
            } else if (quotes != null) {
                result.addAll(gson.fromJson<ArrayList<Quote>>(prefs.getString(Loader.QUOTEURL, null), type))
            }
            return result
        }

        override fun onPostExecute(result: ArrayList<Quote>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                hamers_recyclerview.adapter.notifyDataSetChanged()
            }
            setRefreshing(false)
        }
    }
}