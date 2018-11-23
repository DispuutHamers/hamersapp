package nl.ecci.hamers.ui.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import android.view.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.Beer
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.NewBeerActivity
import nl.ecci.hamers.ui.adapters.BeerAdapter
import org.jetbrains.anko.support.v4.act
import java.util.*

class BeerFragment : HamersListFragment() {

    private val dataSet = ArrayList<Beer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hamers_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.adapter = BeerAdapter(dataSet, act)
        hamers_fab.setOnClickListener { startActivityForResult(Intent(activity, NewBeerActivity::class.java), 1) }

        populateList().execute()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(act, Loader.BEERURL, -1, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        })
        Loader.getData(act, Loader.REVIEWURL, -1, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.beer_menu, menu)
        val searchMenuItem = menu?.findItem(R.id.beer_search)
        val searchView = searchMenuItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean = false

            override fun onQueryTextChange(s: String): Boolean {
                (hamers_list.adapter as BeerAdapter).filter.filter(s.toLowerCase())
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scroll_top -> {
                scrollTop()
                return true
            }
            R.id.sort_name -> {
                sort(nameComparator)
                return true
            }
            R.id.sort_rating -> {
                sort(ratingComparator)
                return true
            }
            R.id.sort_date_asc -> {
                sort(dateASCComparator)
                return true
            }
            R.id.sort_date_desc -> {
                sort(dateDESCComparator)
                return true
            }
            else -> return false
        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity?.title = resources.getString(R.string.navigation_item_beers)
    }

    private fun scrollTop() {
        hamers_list.smoothScrollToPosition(0)
    }

    private fun sortList() {
        if (activity != null) {
            val sortPref = prefs?.getString("beerSort", "")
            when (sortPref) {
                "rating" -> sort(ratingComparator)
                "datumASC" -> sort(dateASCComparator)
                "datumDESC" -> sort(dateDESCComparator)
                else -> sort(nameComparator)
            }
        }
    }

    private fun sort(comparator: Comparator<Beer>) {
        Collections.sort<Beer>(dataSet, comparator)
        notifyAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onRefresh()
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Beer>?>() {

        override fun doInBackground(vararg params: String): ArrayList<Beer>? {
            val type = object : TypeToken<ArrayList<Beer>>() {
            }.type
            val gsonBuilder = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            return if (params.isNotEmpty()) {
                gson.fromJson<ArrayList<Beer>>(params[0], type)
            } else {
                gson.fromJson<ArrayList<Beer>>(prefs?.getString(Loader.BEERURL, null), type)
            }
        }

        override fun onPostExecute(result: ArrayList<Beer>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                notifyAdapter()
            }
            setRefreshing(false)
            sortList()
        }
    }

    companion object {
        private val nameComparator = Comparator<Beer> { beer1, beer2 -> beer1.name.compareTo(beer2.name, ignoreCase = true) }
        private val ratingComparator = Comparator<Beer> { beer1, beer2 ->
            var rating1 = beer1.rating
            var rating2 = beer2.rating

            if (rating1 == "nog niet bekend") {
                rating1 = "-1"
            } else if (rating2 == "nog niet bekend") {
                rating2 = "-1"
            }
            rating2.compareTo(rating1, ignoreCase = true)
        }
        private val dateASCComparator = Comparator<Beer> { beer1, beer2 -> beer1.createdAt.compareTo(beer2.createdAt) }
        private val dateDESCComparator = Comparator<Beer> { beer1, beer2 -> beer2.createdAt.compareTo(beer1.createdAt) }
    }
}