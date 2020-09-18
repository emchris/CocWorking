package com.example.cocworking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cocworking.databinding.ActivitySalaRiunioniBinding
import com.example.cocworking.databinding.EventItemViewBinding
import com.example.cocworking.models.Event
import kotlinx.android.synthetic.main.event_item_view.view.*

class EventAdapter(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.event_item_view, parent, false)
            //EventItemViewBinding.inflate(layoutInflater)
        )
    }

    override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size
    /*
    inner class EventViewHolder(private val binding: EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event) {
            binding.itemEventText.text = event.text
        }
    }
    */

    inner class EventViewHolder constructor(
        eventView: View
    ): RecyclerView.ViewHolder(eventView){

        val itemEventText =  eventView.itemEventText
        val itemEventTime = eventView.itemEventTime

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event) {
            itemEventText.setText(event.text)
            itemEventTime.setText(event.date.hour.toString()+":"+event.date.minute.toString())
        }
    }
}