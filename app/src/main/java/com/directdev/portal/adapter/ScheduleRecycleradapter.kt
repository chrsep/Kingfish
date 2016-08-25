package com.directdev.portal.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.directdev.portal.R
import com.directdev.portal.model.ScheduleModel
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class ScheduleRecycleradapter(context: Context, data: OrderedRealmCollection<ScheduleModel>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<ScheduleModel, ScheduleRecycleradapter.ViewHolder>(context, data, autoUpdate) {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder != null) {
            holder.course.text = data?.get(position)?.courseName
            holder.mode.text = data?.get(position)?.deliveryMode
            holder.room.text = data?.get(position)?.room
            holder.shift.text = data?.get(position)?.startTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_schedule, parent, false))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val course = view.findViewById(R.id.journalCourse) as TextView
        val mode = view.findViewById(R.id.journalMode) as TextView
        val room = view.findViewById(R.id.journalRoom) as TextView
        val shift = view.findViewById(R.id.journalShift) as TextView

    }
}