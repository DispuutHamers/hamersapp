package nl.ecci.hamers.ui.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.User
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.adapters.UserFragmentAdapter
import nl.ecci.hamers.ui.adapters.UserListAdapter
import nl.ecci.hamers.utils.DividerItemDecoration
import org.jetbrains.anko.padding
import java.util.*

class UserListFragment : HamersListFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val dataSet = ArrayList<User>()
    private var exUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hamers_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.addItemDecoration(DividerItemDecoration(requireContext()))
        hamers_list.padding = 0
        hamers_list.adapter = UserListAdapter(dataSet, activity as Context)
        hamers_fab.visibility = View.GONE

        exUser = arguments!!.getBoolean(UserFragmentAdapter.exUser, false)

        populateList().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_menu, menu)
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(requireContext(), Loader.USERURL, -1, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
                return true
            }
            else -> return false
        }
    }

    private fun sort() {
        when (prefs?.getString("userSort", "")) {
            "name" -> sortByUsername()
            "quotecount" -> sortByQuoteCount()
            "reviewcount" -> sortByReviewCount()
        }
        notifyAdapter()
    }

    private fun sortByUsername() {
        val nameComperator = Comparator<User> { user1, user2 -> user1.name.compareTo(user2.name, ignoreCase = true) }
        Collections.sort(dataSet, nameComperator)
        notifyAdapter()
    }

    private fun sortByQuoteCount() {
        val quoteComperator = Comparator<User> { user1, user2 -> user2.quoteCount - user1.quoteCount }
        Collections.sort(dataSet, quoteComperator)
        notifyAdapter()
    }

    private fun sortByReviewCount() {
        val reviewComperator = Comparator<User> { user1, user2 -> user2.reviewCount - user1.reviewCount }
        Collections.sort(dataSet, reviewComperator)
        notifyAdapter()
    }

    private fun sortByBatch() {
        val batchComperator = Comparator<User> { user1, user2 -> user1.batch - user2.batch }
        Collections.sort(dataSet, batchComperator)
        notifyAdapter()
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

            tempList = if (params.isNotEmpty()) {
                gson.fromJson<ArrayList<User>>(params[0], type)
            } else {
                gson.fromJson<ArrayList<User>>(prefs?.getString(Loader.USERURL, null), type)
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
                notifyAdapter()
            }
            sort()
            setRefreshing(false)
        }
    }
}
