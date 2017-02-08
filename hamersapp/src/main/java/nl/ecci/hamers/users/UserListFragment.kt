package nl.ecci.hamers.users

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import java.util.Collections
import java.util.Comparator

import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DividerItemDecoration
import nl.ecci.hamers.helpers.HamersFragment
import nl.ecci.hamers.loader.GetCallback
import nl.ecci.hamers.loader.Loader

import nl.ecci.hamers.MainActivity.prefs

class UserListFragment : HamersFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val dataSet = ArrayList<User>()
    private var adapter: UserListAdapter? = null
    private var exUser: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.user_list_fragment, container, false)
        val userList = view.findViewById(R.id.user_list) as RecyclerView

        setHasOptionsMenu(true)

        val layoutManager = LinearLayoutManager(activity)
        userList.layoutManager = layoutManager
        userList.itemAnimator = DefaultItemAnimator()
        userList.addItemDecoration(DividerItemDecoration(activity))

        adapter = UserListAdapter(dataSet, activity)
        userList.adapter = adapter

        swipeRefreshLayout = view.findViewById(R.id.users_swipe_container) as SwipeRefreshLayout
        initSwiper(userList, layoutManager, swipeRefreshLayout)

        exUser = arguments.getBoolean(UserFragmentPagerAdapter.exUser, false)

        sort()

        populateList().execute()
        onRefresh()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_menu, menu)
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(context, Loader.USERURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }

            override fun onError(error: VolleyError) {
                // Nothing
            }
        }, null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_username -> {
                sortByUsername()
                return true
            }
            R.id.sort_quotes -> {
                sortByQuoteCount()
                return true
            }
            R.id.sort_reviews -> {
                sortByReviewCount()
                return true
            }
            R.id.sort_batch -> {
                sortByBatch()
                return false
            }
            else -> return false
        }
    }

    private fun sort() {
        val sortPref = MainActivity.prefs.getString("userSort", "")
        when (sortPref) {
            "name" -> sortByUsername()
            "quotecount" -> sortByQuoteCount()
            "reviewcount" -> sortByReviewCount()
        }
    }

    private fun sortByUsername() {
        val nameComperator = Comparator<User> { user1, user2 -> user1.name.compareTo(user2.name, ignoreCase = true) }
        Collections.sort(dataSet, nameComperator)
        adapter!!.notifyDataSetChanged()
    }

    private fun sortByQuoteCount() {
        val quoteComperator = Comparator<User> { user1, user2 -> user2.quoteCount - user1.quoteCount }
        Collections.sort(dataSet, quoteComperator)
        adapter!!.notifyDataSetChanged()
    }

    private fun sortByReviewCount() {
        val reviewComperator = Comparator<User> { user1, user2 -> user2.reviewCount - user1.reviewCount }
        Collections.sort(dataSet, reviewComperator)
        adapter!!.notifyDataSetChanged()
    }

    private fun sortByBatch() {
        val batchComperator = Comparator<User> { user1, user2 -> user1.batch - user2.batch }
        Collections.sort(dataSet, batchComperator)
        adapter!!.notifyDataSetChanged()
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<User>>() {
        override fun doInBackground(vararg params: String): ArrayList<User> {
            val result = ArrayList<User>()
            val tempList: ArrayList<User>?
            val type = object : TypeToken<ArrayList<User>>() {

            }.type
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            if (params.isNotEmpty()) {
                tempList = gson.fromJson<ArrayList<User>>(params[0], type)
            } else {
                tempList = gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)
            }

            if (tempList != null) {
                for (user in tempList) {
                    if (exUser && user.member !== User.Member.LID) {
                        result.add(user)
                    } else if (!exUser && user.member === User.Member.LID) {
                        result.add(user)
                    }
                }
            }
            return result
        }

        override fun onPostExecute(result: ArrayList<User>?) {
            if (result != null) {
                dataSet.clear()
                dataSet.addAll(result)
                if (adapter != null) {
                    adapter!!.notifyDataSetChanged()
                }
            }
            sort()
            setRefreshing(false)
        }
    }
}
