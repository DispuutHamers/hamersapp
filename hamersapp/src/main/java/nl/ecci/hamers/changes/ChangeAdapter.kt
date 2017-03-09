package nl.ecci.hamers.changes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_change.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.Utils
import nl.ecci.hamers.users.User
import java.util.*

internal class ChangeAdapter(private val dataSet: ArrayList<Change>, private val context: Context) : RecyclerView.Adapter<ChangeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_change, parent, false)
        return ChangeAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = Utils.getUser(context, dataSet[position].userId)
        holder.bindChange(dataSet[position], user)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindChange(change: Change, user: User) {
            with(change) {

                itemView.change.text = change.newObject
            }
        }
    }
}