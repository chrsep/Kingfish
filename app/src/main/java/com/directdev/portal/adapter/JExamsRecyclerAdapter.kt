package com.directdev.portal.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.ExamModel
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_jexams.view.*

class JExamsRecyclerAdapter(
        context: Context,
        data: OrderedRealmCollection<ExamModel>?,
        autoUpdate: Boolean) :
        RealmRecyclerViewAdapter<ExamModel, JExamsRecyclerAdapter.ViewHolder>(context, data, autoUpdate) {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(data?.get(position) as ExamModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
         ViewHolder(inflater.inflate(R.layout.item_jexams, parent, false))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(item: ExamModel) {
            itemView.journal_exam_chair.text = "Seat " + item.chairNumber
            itemView.journal_exam_room.text = item.room
            itemView.journal_exam_shift.text = item.shift
            itemView.journal_exam_name.text = item.courseName
        }
    }
}
