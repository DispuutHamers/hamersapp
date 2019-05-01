package nl.ecci.hamers.ui.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_user.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.models.User
import nl.ecci.hamers.ui.activities.SingleUserActivity
import nl.ecci.hamers.utils.DataUtils.getGravatarURL
import java.util.*

internal class UserListAdapter(private val dataSet: ArrayList<User>, private val context: Context) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        val vh = ViewHolder(view)

        view.setOnClickListener {
            val intent = Intent(context, SingleUserActivity::class.java)
            intent.putExtra(User.USER, dataSet[vh.adapterPosition].id)
            context.startActivity(intent)
        }

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindUser(user: User) {
            itemView.username.text = user.name
            Glide.with(context).load(getGravatarURL(user.email)).into(itemView.user_image)
        }
    }
}
