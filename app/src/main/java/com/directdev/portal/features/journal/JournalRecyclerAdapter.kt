package com.directdev.portal.features.journal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.models.JournalModel
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_journal.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject

class JournalRecyclerAdapter @Inject constructor(
        val context: Context
) : RealmRecyclerViewAdapter<JournalModel, JournalRecyclerAdapter.ViewHolder>(null, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_journal, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(context, getItem(position) as JournalModel)
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bindData(ctx: Context, item: JournalModel) {
            setHeader(item)
            itemView.recyclerSchedule.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            itemView.recyclerSchedule.isNestedScrollingEnabled = false
            itemView.recyclerSchedule.adapter = SessionRecyclerAdapter(ctx, item.session, true)

            itemView.recyclerJfinances.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            itemView.recyclerJfinances.isNestedScrollingEnabled = false
            itemView.recyclerJfinances.adapter = JFinanceRecyclerAdapter(item.finance, true)

            itemView.recyclerJexams.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            itemView.recyclerJexams.isNestedScrollingEnabled = false
            itemView.recyclerJexams.adapter = JExamsRecyclerAdapter(item.exam, true)
        }

        private fun setHeader(item: JournalModel) {
            val today = DateTime.now().withTimeAtStartOfDay()
            if (item.date == today.toDate()) {
                itemView.header.visibility = View.GONE
                return
            }

            itemView.header.visibility = View.VISIBLE
            if (item.date == today.plusDays(1).toDate()) itemView.txtDay.text = "Tomorrow"
            else itemView.txtDay.text = DateTime(item.date).dayOfWeek().getAsText(Locale.US)
            itemView.txtDate.text = DateTime.parse(item.id.substring(0, 10))
                    .toString(DateTimeFormat.forPattern("dd MMM ''yy"))
        }
    }
}


