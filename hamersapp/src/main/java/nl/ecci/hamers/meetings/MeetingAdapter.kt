package nl.ecci.hamers.meetings

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_meeting.view.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.DataUtils
import java.util.*

class MeetingAdapter(private val dataSet: ArrayList<Meeting>, private val context: Activity) : RecyclerView.Adapter<MeetingAdapter.ViewHolder>() {

    private val ownID: Int = DataUtils.getOwnUser(MainActivity.prefs).id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_meeting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMeeting(dataSet[position])

        holder.view.setOnClickListener {
            try {
                val intent = Intent(context, SingleMeetingActivity::class.java)
                intent.putExtra(Meeting.MEETING, dataSet[position].id)
                context.startActivity(intent)
            } catch (ignored: NullPointerException) {
            }
        }

        if (dataSet[position].userID == ownID) {
            context.registerForContextMenu(holder.view)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindMeeting(meeting: Meeting) {
            with(meeting) {
                itemView.meeting_subject.text = meeting.subject
                itemView.meeting_date.text = MainActivity.appDF2.format(meeting.date)
            }
        }
    }
}
