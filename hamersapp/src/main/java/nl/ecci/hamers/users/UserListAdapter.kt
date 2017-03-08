package nl.ecci.hamers.users

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.user_row.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.Utils.getGravatarURL
import java.util.*

internal class UserListAdapter(private val dataSet: ArrayList<User>, private val context: Context) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        val vh = UserListAdapter.ViewHolder(view)

        view.setOnClickListener {
            val intent = Intent(context, SingleUserActivity::class.java)
            intent.putExtra(User.USER_ID, dataSet[vh.adapterPosition].id)
            context.startActivity(intent)
        }

        return vh
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        holder.bindUser(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindUser(user: User) {
            with(user) {
                itemView.username.text = user.name
                ImageLoader.getInstance().displayImage(getGravatarURL(user.email), itemView.user_image, AnimateFirstDisplayListener())
            }
        }
    }
}
