package nl.ecci.hamers.ui.fragments

import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import kotlinx.android.synthetic.main.fragment_hamers_list.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.Quote
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.adapters.QuoteAdapter
import nl.ecci.hamers.utils.DividerItemDecoration
import org.jetbrains.anko.padding
import java.util.*


class QuoteFragment : HamersListFragment(), DialogInterface.OnDismissListener {

    private val dataSet = ArrayList<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_hamers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.padding = 0
        context?.let {
            hamers_list.addItemDecoration(DividerItemDecoration(it))
            hamers_list.adapter = QuoteAdapter(dataSet, it)
        }

        // When user presses "+" in QuoteListFragment, start new dialog with NewQuoteFragment
        hamers_fab.setOnClickListener {
            NewQuoteFragment().show(childFragmentManager, "quotes")
        }

        populateList().execute()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(requireContext(), Loader.QUOTEURL, -1, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quote_menu, menu)
        val menuItem = menu.findItem(R.id.quote_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean = false

            override fun onQueryTextChange(s: String): Boolean {
                (hamers_list.adapter as QuoteAdapter).filter.filter(s.toLowerCase())
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity?.title = resources.getString(R.string.navigation_item_quotes)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scroll_top -> {
                scrollTop()
                true
            }
            else -> false
        }
    }

    private fun scrollTop() {
        view?.hamers_list?.smoothScrollToPosition(0)
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

            val quotes = prefs?.getString(Loader.QUOTEURL, null)

            if (params.isNotEmpty()) {
                result.addAll(gson.fromJson<ArrayList<Quote>>(params[0], type))
            } else if (quotes != null) {
                result.addAll(gson.fromJson<ArrayList<Quote>>(quotes, type))
            }
            return result
        }

        override fun onPostExecute(result: ArrayList<Quote>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                notifyAdapter()
            }
            setRefreshing(false)
        }
    }
}