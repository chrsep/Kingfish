package com.directdev.portal.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.SessionModel
import com.directdev.portal.utils.readPref
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class SessionRecyclerAdapter(
        context: Context,
        data: OrderedRealmCollection<SessionModel>?,
        autoUpdate: Boolean) :
        RealmRecyclerViewAdapter<SessionModel, SessionRecyclerAdapter.ViewHolder>(context, data, autoUpdate) {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(data?.get(position) as SessionModel, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(inflater.inflate(R.layout.item_sessions, parent, false))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(session: SessionModel, ctx: Context) {
            val color: String
            itemView.journalCourse.text = session.courseName
            itemView.journalRoom.text = session.room
            itemView.journalShift.text = session.startTime
            if (session.deliveryMode == "GSLC") {
                itemView.journalMode.text = "GSLC"
                itemView.journalShift.visibility = View.GONE
                color = "#f44336"
            } else {
                if (!ctx.readPref(R.string.campus_setting, false, "com.directdev.portal_preferences")) {
                    itemView.journalCampus.text = session.locationId
                }
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