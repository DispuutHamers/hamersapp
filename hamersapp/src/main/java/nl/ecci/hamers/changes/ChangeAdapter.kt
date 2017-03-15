package nl.ecci.hamers.changes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.row_change.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.beers.Beer
import nl.ecci.hamers.events.Event
import nl.ecci.hamers.events.SignUp
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.DataUtils.getGravatarURL
import java.util.*

internal class ChangeAdapter(private val context: Context, private val dataSet: ArrayList<Change>) : RecyclerView.Adapter<ChangeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_change, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindChange(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

    internal inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindChange(change: Change) {
            val user = DataUtils.getUser(context, change.userId)

            with(change) {
                // Set user image
                ImageLoader.getInstance().displayImage(getGravatarURL(user.email), itemView.user_image, AnimateFirstDisplayListener())

                when (change.itemType) {
                    Change.ItemType.QUOTE -> bindQuote(change)
                    Change.ItemType.EVENT -> bindEvent(change)
                    Change.ItemType.SIGNUP -> bindSignUp(change)
                    Change.ItemType.BEER -> bindBeer(change)
                    Change.ItemType.REVIEW -> {
                        // TODO
                    }
                    Change.ItemType.NEWS -> {
                        // TODO
                    }
                    Change.ItemType.USER -> {
                        // TODO
                    }
                    Change.ItemType.STICKER -> bindSticker(change)
                    Change.ItemType.DEVICE -> {
                        // Do nothing
                    }
                }
            }
        }

        private fun bindQuote(change: Change) {
            when (change.event) {
                Change.Event.CREATE ->  itemView.change.text = context.getString(R.string.change_new_quote)
                Change.Event.UPDATE ->  itemView.change.text = context.getString(R.string.change_update_quote)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_destroy_quote)
            }
        }

        private fun bindEvent(change: Change) {
            val event: Event = DataUtils.getEvent(context, change.itemId)

            when (change.event) {
                Change.Event.CREATE ->  itemView.change.text = context.getString(R.string.change_new_event)
                Change.Event.UPDATE ->  itemView.change.text = context.getString(R.string.change_update_event)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_destroy_event)
            }

            itemView.change_description.text = event.title
        }

        private fun bindSignUp(change: Change) {
            val signUp: SignUp = DataUtils.getSignup(context, change.itemId)
            val event: Event = DataUtils.getEvent(context, signUp.eventID)

            when (change.event) {
                Change.Event.CREATE -> {
                    if (signUp.isAttending) {
                        itemView.change.text = context.getString(R.string.change_new_signup_true)
                    } else {
                        itemView.change.text = context.getString(R.string.change_new_signup_false)
                    }
                }
                Change.Event.UPDATE ->  itemView.change.text = context.getString(R.string.change_update_signup)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_destroy_signup)
            }

            itemView.change_description.text = event.title
        }

        private fun bindBeer(change: Change) {
            val beer: Beer = DataUtils.getBeer(context, change.itemId)

            when (change.event) {
                Change.Event.CREATE ->  itemView.change.text = context.getString(R.string.change_new_beer)
                Change.Event.UPDATE ->  itemView.change.text = context.getString(R.string.change_update_beer)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_destroy_beer)
            }

            itemView.change_description.text = beer.name
        }


        private fun bindSticker(change: Change) {
            when (change.event) {
                Change.Event.CREATE ->  itemView.change.text = context.getString(R.string.change_new_sticker)
                Change.Event.UPDATE ->  itemView.change.text = context.getString(R.string.change_update_sticker)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_destroy_sticker)
            }
            itemView.change_description.visibility = View.GONE
        }
    }
}