package nl.ecci.hamers.ui.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.row_imageview.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.models.Quote
import nl.ecci.hamers.models.User
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.SingleUserActivity
import nl.ecci.hamers.utils.DataUtils.getGravatarURL
import nl.ecci.hamers.utils.DataUtils.getUser
import java.util.*

internal class QuoteAdapter(private val dataSet: ArrayList<Quote>, private val context: Context) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>(), Filterable {
    private var filteredDataSet = dataSet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_imageview, parent, false)
        val vh = ViewHolder(view)
        val imageView = view.findViewById<CircleImageView>(R.id.row_imageview_image)

        imageView.setOnClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val intent = Intent(context, SingleUserActivity::class.java)
                intent.putExtra(User.USER, filteredDataSet[vh.adapterPosition].userID)
                context.startActivity(intent)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getUser(context, filteredDataSet[position].userID)
        holder.bindQuote(filteredDataSet[position], user)
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()

                // If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = dataSet
                    results.count = dataSet.size
                } else {
                    val filterResultsData = dataSet.filterTo(ArrayList()) { it.text.toLowerCase().contains(charSequence) || getUser(context, it.userID).name.toLowerCase().contains(charSequence) }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredDataSet = filterResults.values as ArrayList<Quote>
                notifyDataSetChanged()
            }
        }
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindQuote(quote: Quote, user: User) {
            with(quote) {
                itemView.row_imageview_title_textview.text = quote.text
                itemView.row_imageview_subtitle_textview.text = MainActivity.appDF.format(date)

                Glide.with(context).load(getGravatarURL(user.email)).into(itemView.row_imageview_image)
            }
        }
    }
}
