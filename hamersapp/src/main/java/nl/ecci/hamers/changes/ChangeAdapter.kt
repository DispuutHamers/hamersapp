package nl.ecci.hamers.changes

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.row_change.view.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.beers.Beer
import nl.ecci.hamers.beers.SingleBeerActivity
import nl.ecci.hamers.events.Event
import nl.ecci.hamers.events.SingleEventActivity
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.DataUtils.getGravatarURL
import nl.ecci.hamers.users.SingleUserActivity
import nl.ecci.hamers.users.User
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

    internal inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        override fun onClick(v: View?) {
            val change = dataSet[layoutPosition]

            if (change.itemType != null) {
                val intent: Intent?
                when (change.itemType) {
                    Change.ItemType.QUOTE -> {
                        swapFragment(0)
                        return
                    }
                    Change.ItemType.EVENT -> {
                        intent = Intent(context, SingleEventActivity::class.java)
                        intent.putExtra(Event.EVENT, change.itemId)
                    }
                    Change.ItemType.SIGNUP -> {
                        intent = Intent(context, SingleEventActivity::class.java)
                        val event = DataUtils.getSignUp(context, change.itemId)
                        intent.putExtra(Event.EVENT, event.id)
                    }
                    Change.ItemType.BEER -> {
                        intent = Intent(context, SingleBeerActivity::class.java)
                        intent.putExtra(Beer.BEER, change.itemId)
                    }
                    Change.ItemType.REVIEW -> {
                        intent = Intent(context, SingleBeerActivity::class.java)
                        val review = DataUtils.getReview(context, change.itemId)
                        intent.putExtra(Beer.BEER, review.beerID)
                    }
                    Change.ItemType.NEWS -> {
                        swapFragment(3)
                        return
                    }
                    Change.ItemType.USER -> {
                        intent = Intent(context, SingleUserActivity::class.java)
                        intent.putExtra(User.USER, change.itemId)
                    }
                    else -> {
                        // Do nothing
                        return
                    }
                }
                context.startActivity(intent)
            }
        }

        fun bindChange(change: Change) {
            val user = DataUtils.getUser(context, change.userId)
            itemView.setOnClickListener(this)

            with(change) {
                // Set user image
                ImageLoader.getInstance().displayImage(getGravatarURL(user.email), itemView.user_image, AnimateFirstDisplayListener())

                if (change.itemType != null)
                    when (change.itemType) {
                        Change.ItemType.QUOTE -> bindQuote(change)
                        Change.ItemType.EVENT -> bindEvent(change)
                        Change.ItemType.SIGNUP -> bindSignUp(change)
                        Change.ItemType.BEER -> bindBeer(change)
                        Change.ItemType.REVIEW -> bindReview(change)
                        Change.ItemType.NEWS -> bindNews(change)
                        Change.ItemType.USER -> bindUser(change)
                        Change.ItemType.STICKER -> bindSticker(change)
                        Change.ItemType.NICKNAME -> bindNickname(change)
                        else -> {
                            // Do nothing
                        }
                    }

                itemView.change_timestamp.text = MainActivity.appDF.format(createdAt)
            }
        }

        private fun bindQuote(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_quote_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_quote_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_quote_destroy)
            }
        }

        private fun bindEvent(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_event_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_event_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_event_destroy)
            }
        }

        private fun bindSignUp(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_signup_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_signup_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_signup_destroy)
            }
        }

        private fun bindBeer(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_beer_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_beer_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_beer_destroy)
            }
        }

        private fun bindReview(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_review_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_review_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_review_destroy)
            }
        }

        private fun bindNews(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_news_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_news_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_news_destroy)
            }
        }

        private fun bindUser(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_user_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_user_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_user_destroy)
            }
        }

        private fun bindSticker(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_sticker_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_sticker_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_sticker_destroy)
            }
        }

        private fun bindNickname(change: Change) {
            when (change.event) {
                Change.Event.CREATE -> itemView.change.text = context.getString(R.string.change_nick_new)
                Change.Event.UPDATE -> itemView.change.text = context.getString(R.string.change_nick_update)
                Change.Event.DESTROY -> itemView.change.text = context.getString(R.string.change_nick_destroy)
            }
        }
    }

    private fun swapFragment(i: Int) {
        val activity = context as MainActivity
        val menuItem = activity.navigation_view.menu.getItem(i)
        MainActivity.selectItem(activity, menuItem)
        menuItem.isChecked = true
    }
}