package nl.ecci.hamers.meetings

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class MeetingFragment : HamersListFragment() {

    private val dataSet = ArrayList<Meeting>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_hamers_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.adapter = MeetingAdapter(dataSet, activity)
        hamers_fab.setOnClickListener { startActivityForResult(Intent(activity, NewMeetingActivity::class.java), 1) }

        onRefresh()
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(getContext(), Loader.MEETINGURL, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity.title = resources.getString(R.string.navigation_item_meetings)
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Meeting>>() {
        override fun doInBackground(vararg params: String): ArrayList<Meeting> {
            val result: ArrayList<Meeting>
            val type = object : TypeToken<ArrayList<Meeting>>() {
            }.type

            val gsonBuilder = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            if (params.isNotEmpty()) {
                result = gson.fromJson<ArrayList<Meeting>>(params[0], type)
            } else {
                result = gson.fromJson<ArrayList<Meeting>>(prefs.getString(Loader.MEETINGURL, null), type)
            }
            return result
        }

        override fun onPostExecute(result: ArrayList<Meeting>) {
            if (!result.isEmpty()) {
                dataSet.clear()
                dataSet.addAll(result)
                notifyAdapter()
            }
            setRefreshing(false)
        }
    }
}
