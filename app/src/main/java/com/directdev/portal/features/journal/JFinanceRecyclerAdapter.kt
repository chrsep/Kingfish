package com.directdev.portal.features.journal

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.models.FinanceModel
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_jfinances.view.*
import java.text.NumberFormat
import java.util.*

class JFinanceRecyclerAdapter(
        data: OrderedRealmCollection<FinanceModel>?,
        autoUpdate: Boolean) :
        RealmRecyclerViewAdapter<FinanceModel, JFinanceRecyclerAdapter.ViewHolder>(data, autoUpdate) {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(data?.get(position) as FinanceModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_jfinances, parent, false))

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindData(item: FinanceModel) {
            itemView.journal_finance_description.text = item.description
            itemView.journal_finance_amount.text = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(item.chargeAmount.toFloat())
        }
    }
}
