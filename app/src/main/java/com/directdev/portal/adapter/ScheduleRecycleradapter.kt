package com.directdev.portal.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.SessionModel
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.recycler_schedule.view.*

class ScheduleRecycleradapter(context: Context, data: OrderedRealmCollection<SessionModel>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<SessionModel, ScheduleRecycleradapter.ViewHolder>(context, data, autoUpdate) {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(data?.get(position) as SessionModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_schedule, parent, false))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(session: SessionModel) {
            val color: String
            itemView.journalCourse.text = session.courseName
            itemView.journalRoom.text = session.room
            itemView.journalShift.text = session.startTime
            if (session.deliveryMode == "GSLC") {
                itemView.journalMode.text = "GSLC"
                itemView.journalShift.visibility = View.GONE
                color = "#f44336"
            } else {
                itemView.journalMode.text = session.typeId
                color = when (session.typeId) {
                    "LEC" -> "#ffeb3b"
                    "LAB" -> "#4caf50"
                    "CL" -> "#00B0FF"
                    else -> "#00E676"
                }
            }
            itemView.typeId.setBackgroundColor(Color.parseColor(color))
        }
    }
}