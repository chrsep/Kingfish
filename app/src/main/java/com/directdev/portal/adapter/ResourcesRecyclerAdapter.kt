package com.directdev.portal.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.ResModel
import com.directdev.portal.model.ResResourcesModel
import kotlinx.android.synthetic.main.item_resources.view.*
import org.jetbrains.anko.layoutInflater

/**
 * Created by chris on 9/15/2016.
 */
class ResourcesRecyclerAdapter(val context: Context, val data: List<String>, val resources: ResModel) : RecyclerView.Adapter<ResourcesRecyclerAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(context.layoutInflater.inflate(R.layout.item_resources, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val outlines = resources.resources.filter { it.courseOutlineTopicID == data[position] }
        holder?.bindData(context, outlines)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(ctx: Context, item: List<ResResourcesModel>) {
            if (item.size == 0) return
            itemView.resSession.text = item[0].sessionIDNUM
            itemView.resTopic.text = item[0].courseOutlineTopic
            itemView.presentationDownload.supportBackgroundTintList = ColorStateList.valueOf(Color.parseColor(ctx.getString(R.color.colorAccent)))
        }
    }
}