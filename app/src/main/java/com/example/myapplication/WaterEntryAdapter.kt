package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.Date

class WaterEntryAdapter : ListAdapter<WaterEntry, WaterEntryAdapter.EntryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.entryDate)
        private val amountText: TextView = itemView.findViewById(R.id.entryAmount)
        private val noteText: TextView = itemView.findViewById(R.id.entryNote)

        fun bind(entry: WaterEntry) {
            dateText.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(Date(entry.recordedAt))
            amountText.text = itemView.context.getString(R.string.entry_amount_format, entry.amountMl)
            noteText.text = entry.note
            noteText.visibility = if (entry.note.isBlank()) View.GONE else View.VISIBLE
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WaterEntry>() {
            override fun areItemsTheSame(oldItem: WaterEntry, newItem: WaterEntry): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WaterEntry, newItem: WaterEntry): Boolean =
                oldItem.amountMl == newItem.amountMl &&
                    oldItem.note == newItem.note &&
                    oldItem.recordedAt == newItem.recordedAt
        }
    }
}
