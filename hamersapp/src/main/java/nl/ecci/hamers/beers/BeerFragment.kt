package nl.ecci.hamers.beers

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
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.HamersListFragment
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader
import java.util.*

class BeerFragment : HamersListFragment(){

    private val dataSet = ArrayList<Beer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_hamers_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_recyclerview.adapter = BeerAdapter(dataSet, activity)
        hamers_fab.setOnClickListener { startActivityForResult(Intent(activity, NewBeerActivity::class.java), 1) }

        populateList().execute()
        onRefresh()
        sortList()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(context, Loader.BEERURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
        Loader.getData(context, Loader.REVIEWURL, null, null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.beer_menu, menu)
        val searchMenuItem = menu?.findItem(R.id.beer_search)
        val searchView = MenuItemCompat.getActionView(searchMenuItem) as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                (hamers_recyclerview.adapter as BeerAdapter).filter.filter(s.toLowerCase())
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
        activity.title = resources.getString(R.string.navigation_item_beers)
    }

    private fun scrollTop() {
        hamers_recyclerview.smoothScrollToPosition(0)
    }

    private fun sortList() {
        if (activity != null)
            if (prefs != null) {
                val sortPref = prefs.getString("beerSort", "")
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

    override fun onDestroy() {
        super.onDestroy()
        AnimateFirstDisplayListener.displayedImages.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onRefresh()
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Beer>>() {
        override fun doInBackground(vararg params: String): ArrayList<Beer> {
            val result: ArrayList<Beer>
            val type = object : TypeToken<ArrayList<Beer>>() {
            }.type
            val gsonBuilder = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            if (params.isNotEmpty()) {
                result = gson.fromJson<ArrayList<Beer>>(params[0], type)
            } else {
                result = gson.fromJson<ArrayList<Beer>>(prefs.getString(Loader.BEERURL, null), type)
            }
            return result
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
        private val nameComparator = Comparator<Beer> { beer1, beer2 -> beer1.name!!.compareTo(beer2.name!!, ignoreCase = true) }
        private val ratingComparator = Comparator<Beer> { beer1, beer2 ->
            var rating1 = beer1.rating
            var rating2 = beer2.rating

            if (rating1 == "nog niet bekend") {
                rating1 = "-1"
            } else if (rating2 == "nog niet bekend") {
                rating2 = "-1"
            }
            rating2!!.compareTo(rating1!!, ignoreCase = true)
        }
        private val dateASCComparator = Comparator<Beer> { beer1, beer2 -> beer1.createdAt.compareTo(beer2.createdAt) }
        private val dateDESCComparator = Comparator<Beer> { beer1, beer2 -> beer2.createdAt.compareTo(beer1.createdAt) }
    }
}