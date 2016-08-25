package com.directdev.portal.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.ActivityDateModel
import com.directdev.portal.model.ScheduleModel
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.recycler_journal.view.*
import org.joda.time.DateTime
import java.util.*

class JournalRecyclerAdapter(val realm: Realm, context: Context, data: OrderedRealmCollection<ActivityDateModel>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<ActivityDateModel, JournalRecyclerAdapter.ViewHolder>(context, data, autoUpdate) {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.recycler_journal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val schedules = realm
                .where(ScheduleModel::class.java)
                .equalTo("date", data?.get(position)?.id)
                .findAll()

        holder?.bindData(getItem(position) as ActivityDateModel)
        holder?.listSchedules(context, schedules)

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(item: ActivityDateModel) {
            itemView.txtDate.text = item.id.substring(0, 10)
            itemView.txtDay.text = DateTime(item.date).dayOfWeek().getAsText(Locale.US)
        }

        fun listSchedules(ctx: Context, item: RealmResults<ScheduleModel>) {
            itemView.recyclerSchedule.layoutManager = LinearLayoutManager(ctx)
            itemView.recyclerSchedule.adapter = ScheduleRecycleradapter(ctx, item, true)
        }
    }
}


