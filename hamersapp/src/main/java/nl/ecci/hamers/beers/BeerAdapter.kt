package nl.ecci.hamers.beers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware
import kotlinx.android.synthetic.main.row_beer.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.SingleImageActivity
import nl.ecci.hamers.helpers.Utils
import java.util.*

internal class BeerAdapter(private val dataSet: ArrayList<Beer>, private val context: Context) : RecyclerView.Adapter<BeerAdapter.ViewHolder>(), Filterable {

    private var filteredDataSet: ArrayList<Beer> = dataSet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_beer, parent, false)
        val vh = ViewHolder(view)

        view.setOnClickListener {
            val activity = context as Activity
            val imageTransitionName = context.getString(R.string.transition_single_image)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view.beer_image, imageTransitionName)
            val intent = Intent(context, SingleBeerActivity::class.java)
            intent.putExtra(Beer.BEER, filteredDataSet[vh.adapterPosition].id)
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }

        view.beer_image.setOnClickListener {
            val beer = filteredDataSet[vh.adapterPosition]
            val activity = context as Activity
            val transitionName = context.getString(R.string.transition_single_image)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view.beer_image, transitionName)
            if (beer.imageURL.isNotEmpty()) {
                val intent = Intent(context, SingleImageActivity::class.java)
                intent.putExtra(Beer.BEER, GsonBuilder().create().toJson(beer, Beer::class.java))
                ActivityCompat.startActivity(activity, intent, options.toBundle())
            } else {
                Utils.showToast(context, context.getString(R.string.no_image), Toast.LENGTH_SHORT)
            }
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindBeer(filteredDataSet[position])
    }

    override fun getItemCount() = filteredDataSet.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val results = Filter.FilterResults()

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = dataSet
                    results.count = dataSet.size
                } else {
                    val filterResultsData = dataSet.filter {
                        it.name!!.toLowerCase().contains(charSequence)
                                || it.brewer!!.toLowerCase().contains(charSequence)
                                || it.brewer!!.toLowerCase().contains(charSequence)
                                || it.percentage!!.toLowerCase().contains(charSequence)
                                || it.kind!!.toLowerCase().contains(charSequence)
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filteredDataSet = filterResults.values as ArrayList<Beer>
                notifyDataSetChanged()
            }
        }
    }

    internal class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindBeer(beer: Beer) {
            with(beer) {
                itemView.name_textview.text = name
                itemView.kind_textview.text = String.format("%s (%s)", kind, percentage)
                itemView.brewer_textview.text = brewer
                itemView.country_textview.text = country
                itemView.rating_textview.text = rating
                ImageLoader.getInstance().displayImage(imageURL, ImageViewAware(itemView.beer_image, false), AnimateFirstDisplayListener())
            }
        }
    }
}
