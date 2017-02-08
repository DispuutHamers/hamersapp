package nl.ecci.hamers.users

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener
import nl.ecci.hamers.helpers.Utils.getGravatarURL
import java.util.*

internal class UserListAdapter(private val dataSet: ArrayList<User>, private val context: Context) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private val imageLoader: ImageLoader = ImageLoader.getInstance()
    private var animateFirstListener: AnimateFirstDisplayListener = AnimateFirstDisplayListener()

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
        holder.userName.text = dataSet[position].name

        val url = getGravatarURL(dataSet[position].email)
        imageLoader.displayImage(url, holder.userImage, animateFirstListener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.user_image) as ImageView
        val userName: TextView = view.findViewById(R.id.username) as TextView
    }
}
