package com.directdev.portal.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.ScheduleModel
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.recycler_schedule.view.*

class ScheduleRecycleradapter(context: Context, data: OrderedRealmCollection<ScheduleModel>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<ScheduleModel, ScheduleRecycleradapter.ViewHolder>(context, data, autoUpdate) {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(data?.get(position) as ScheduleModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_schedule, parent, false))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(data: ScheduleModel) {
            val color: String
            itemView.journalCourse.text = data.courseName
            itemView.journalRoom.text = data.room
            itemView.journalShift.text = data.startTime
            if (data.deliveryMode == "GSLC") {
                itemView.journalMode.text = "GSLC"
                color = "#f44336"
            } else {
                itemView.journalMode.text = data.typeId
                color = when (data.typeId) {
                    "LEC" -> "#ffeb3b"
                    "LAB" -> "#4caf50"
                    "CL" -> "#00B0FF"
                    else -> "#00E676"
                }
            }
            itemView.typeId.setColorFilter(Color.parseColor(color))
        }
    }
}