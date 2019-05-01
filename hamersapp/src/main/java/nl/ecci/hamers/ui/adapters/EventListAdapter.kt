package nl.ecci.hamers.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_event.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.models.Event
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.SingleEventActivity
import nl.ecci.hamers.utils.DataUtils
import nl.ecci.hamers.utils.Utils.toHtml
import java.util.*

internal class EventListAdapter(private val context: Context, private val dataSet: ArrayList<Event>) : RecyclerView.Adapter<EventListAdapter.ViewHolder>(), Filterable {

    private var filteredDataSet: ArrayList<Event> = dataSet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_event, parent, false)
        val vh = ViewHolder(view)

        view.setOnClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val intent = Intent(context, SingleEventActivity::class.java)
                intent.putExtra(Event.EVENT, filteredDataSet[vh.adapterPosition].id)
                context.startActivity(intent)
            }
        }
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindEvent(filteredDataSet[position])
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = dataSet
                    results.count = dataSet.size
                } else {
                    val filterResultsData = dataSet.filterTo(ArrayList<Event>()) { it.title.toLowerCase().contains(charSequence) || it.description.toLowerCase().contains(charSequence) }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredDataSet = filterResults.values as ArrayList<Event>
                notifyDataSetChanged()
            }
        }
    }

    internal inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindEvent(event: Event) {
            itemView.event_title.text = event.title
            var description = event.description
            if (description.length > 256)
                description = description.substring(0, 256)
            itemView.event_beschrijving.text = toHtml(description)
            itemView.event_date.text = MainActivity.appDTF.format(event.date)
            if (event.location.isEmpty()) {
                itemView.event_location.visibility = View.GONE
            } else {
                itemView.event_location.text = event.location
            }

            val signUps = event.signUps
            val userID = DataUtils.getOwnUser(context).id
            var aanwezig: Boolean? = null
            signUps.indices
                    .map { signUps[it] }
                    .filter { it.userID == userID }
                    .forEach { aanwezig = it.isAttending }

            if (aanwezig != null) {
                if (aanwezig as Boolean) {
                    itemView.thumbs.setImageResource(R.drawable.ic_thumbs_up)
                } else {
                    itemView.thumbs.setImageResource(R.drawable.ic_thumbs_down)
                }
            } else {
                itemView.thumbs.setImageResource(R.drawable.ic_questionmark)
            }
        }
    }
}
