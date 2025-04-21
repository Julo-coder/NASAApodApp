package com.example.nasaproject.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nasaproject.R

class DatesAdapter(
    private val onDateClick: (String) -> Unit,
    private val onDateLongClick: (String) -> Unit
) : ListAdapter<String, DatesAdapter.DateViewHolder>(DateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view, onDateClick, onDateLongClick)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DateViewHolder(
        itemView: View,
        private val onDateClick: (String) -> Unit,
        private val onDateLongClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateText)

        fun bind(date: String) {
            dateText.text = date
            itemView.setOnClickListener { onDateClick(date) }
            itemView.setOnLongClickListener {
                onDateLongClick(date)
                true // Return true to indicate the long click was handled
            }
        }
    }
}

class DateDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}