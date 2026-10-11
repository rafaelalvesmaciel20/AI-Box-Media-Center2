package com.aibox.mediacenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AppItem(val name: String, val packageName: String, val icon: android.graphics.drawable.Drawable, val category: AppCategory)
enum class AppCategory { VIDEO, TV, AUDIO, OTHER }

class AppAdapter(private var items: List<AppItem>, private val onClick: (AppItem) -> Unit, private val onLongClick: (AppItem) -> Unit) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {
 class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val icon: ImageView = view.findViewById(R.id.appIcon)
  val name: TextView = view.findViewById(R.id.appName)
 }
 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder = AppViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))
 override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
  val item = items[position]; holder.icon.setImageDrawable(item.icon); holder.name.text = item.name
  holder.itemView.setOnClickListener { onClick(item) }
  holder.itemView.setOnLongClickListener { onLongClick(item); true }
 }
 override fun getItemCount(): Int = items.size
 fun submitItems(newItems: List<AppItem>) { items = newItems; notifyDataSetChanged() }
}
