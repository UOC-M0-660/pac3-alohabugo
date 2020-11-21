package edu.uoc.pac3.twitch.streams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.uoc.pac3.R
import edu.uoc.pac3.data.streams.Stream

class StreamsListAdapter(private var streams: ArrayList<Stream>): RecyclerView.Adapter<StreamsListAdapter.ViewHolder>() {

    fun addStreams(streamsList: List<Stream>) {
        //añadimos el listado de streams
        this.streams.addAll(streamsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.stream_view, parent, false)
        return ViewHolder(view)
    }

    // Binds re-usable View for a given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stream = streams[position]
        holder.titleView.text = stream.title
        holder.userNameView.text = stream.userName
        val thumbnailUrl = stream.thumbnailUrl?.replace("{width}", "200")?.replace("{height}", "200")
        Glide.with(holder.view.context)
                .load(thumbnailUrl)
                .into(holder.thumbnailImageView)
    }

    // Returns total items in Adapter
    override fun getItemCount(): Int {
        return streams.size
    }

    // Holds an instance to the view for re-use
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.title)
        val userNameView: TextView = view.findViewById(R.id.userName)
        val thumbnailImageView: ImageView = view.findViewById(R.id.user_img)
    }
}