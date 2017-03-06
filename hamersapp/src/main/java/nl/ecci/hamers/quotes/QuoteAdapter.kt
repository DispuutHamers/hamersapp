package nl.ecci.hamers.quotes

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.quote_row.view.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.Utils.getGravatarURL
import nl.ecci.hamers.helpers.Utils.getUser
import nl.ecci.hamers.users.SingleUserActivity
import nl.ecci.hamers.users.User
import java.util.*

internal class QuoteAdapter(private val dataSet: ArrayList<Quote>, private val context: Context) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>(), Filterable {
    private var filteredDataSet = dataSet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quote_row, parent, false)
        val vh = ViewHolder(view)
        val imageView = view.findViewById(R.id.quote_image)

        imageView.setOnClickListener {
            val position = vh.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val intent = Intent(context, SingleUserActivity::class.java)
                intent.putExtra(User.USER_ID, filteredDataSet[vh.adapterPosition].userID)
                context.startActivity(intent)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindQuote(filteredDataSet[position])
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val results = Filter.FilterResults()

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = dataSet
                    results.count = dataSet.size
                } else {
                    val filterResultsData = dataSet.filter { it.text.toLowerCase().contains(charSequence) || getUser(MainActivity.prefs, it.userID).name.toLowerCase().contains(charSequence) }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filteredDataSet = filterResults.values as ArrayList<Quote>
                notifyDataSetChanged()
            }
        }
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindQuote(quote: Quote) {
            with(quote) {
                itemView.quote_body.text = quote.text
                itemView.quote_date.text = MainActivity.appDF.format(date)

                val user = getUser(MainActivity.prefs, quote.userID)
                ImageLoader.getInstance().displayImage(getGravatarURL(user.email), itemView.quote_image, AnimateFirstDisplayListener())
            }
        }
    }
}
