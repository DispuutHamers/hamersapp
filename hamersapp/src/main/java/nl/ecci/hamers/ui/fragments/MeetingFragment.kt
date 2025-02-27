package nl.ecci.hamers.ui.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import nl.ecci.hamers.R
import nl.ecci.hamers.data.GetCallback
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.Meeting
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.NewMeetingActivity
import nl.ecci.hamers.ui.adapters.MeetingAdapter
import java.util.*

class MeetingFragment : HamersListFragment() {

    private val dataSet = ArrayList<Meeting>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hamers_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hamers_list.adapter = MeetingAdapter(dataSet, activity!!)
        hamers_fab.setOnClickListener { startActivityForResult(Intent(activity, NewMeetingActivity::class.java), 1) }
    }

    override fun onRefresh() {
        setRefreshing(true)
        Loader.getData(requireContext(), Loader.MEETINGURL, -1, object : GetCallback {
            override fun onSuccess(response: String) {
                populateList().execute(response)
            }
        }, null)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
        activity?.title = resources.getString(R.string.navigation_item_meetings)
    }

    private inner class populateList : AsyncTask<String, Void, ArrayList<Meeting>>() {
        override fun doInBackground(vararg params: String): ArrayList<Meeting> {
            val result: ArrayList<Meeting>
            val type = object : TypeToken<ArrayList<Meeting>>() {
            }.type

            val gsonBuilder = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern())
            val gson = gsonBuilder.create()

            result = if (params.isNotEmpty()) {
                gson.fromJson<ArrayList<Meeting>>(params[0], type)
            } else {
                gson.fromJson<ArrayList<Meeting>>(prefs?.getString(Loader.MEETINGURL, null), type)
            }
            return result
        }

        override fun onPostExecute(result: ArrayList<Meeting>) {
            if (result.isNotEmpty()) {
                dataSet.clear()
                dataSet.addAll(result)
                notifyAdapter()
            }
            setRefreshing(false)
        }
    }
}
