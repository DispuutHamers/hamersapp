package nl.ecci.hamers.quotes

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.util.ArrayList

import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DividerItemDecoration
import nl.ecci.hamers.helpers.HamersFragment
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader

import nl.ecci.hamers.MainActivity.prefs

class QuoteFragment : HamersFragment(), DialogInterface.OnDismissListener {

    private val dataSet = ArrayList<Quote>()
    private var adapter: QuoteAdapter? = null
    private var quoteList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.hamers_fragment, container, false)
        quoteList = view.findViewById(R.id.hamers_recyclerview) as RecyclerView

        setHasOptionsMenu(true)

        val mLayoutManager = LinearLayoutManager(activity)
        quoteList!!.layoutManager = mLayoutManager
        quoteList!!.addItemDecoration(DividerItemDecoration(activity))

        adapter = QuoteAdapter(dataSet, context)
        quoteList!!.adapter = adapter

        swipeRefreshLayout = view.findViewById(R.id.hamers_swipe_container) as SwipeRefreshLayout
        initSwiper(quoteList, mLayoutManager, swipeRefreshLayout)

        // When user presses "+" in QuoteListFragment, start new dialog with NewQuoteFragment
        val newQuoteButton = view.findViewById(R.id.hamers_fab) as FloatingActionButton
        newQuoteButton.setOnClickListener {
            val newQuoteFragment = NewQuoteFragment()
            newQuoteFragment.show(childFragmentManager, "quotes")
        }

        populateList().execute()
        onRefresh()

        return view
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.quote_menu, menu)
        val menuItem = menu!!.findItem(R.id.quote_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                adapter!!.filter.filter(s.toLowerCase())
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.scroll_top -> {
                scrollTop()
                return true
            }
            else -> return false
        }
    }

    private fun scrollTop() {
        quoteList!!.smoothScrollToPosition(0)
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
                if (adapter != null) {
                    adapter!!.notifyDataSetChanged()
                }
            }
            setRefreshing(false)
        }
    }
}