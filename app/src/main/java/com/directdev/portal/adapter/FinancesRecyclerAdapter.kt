package com.directdev.portal.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.FinanceModel
import com.directdev.portal.utils.readPref
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_finances.view.*
import kotlinx.android.synthetic.main.item_finances_header.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import java.text.NumberFormat
import java.util.*

class FinancesRecyclerAdapter(val realm: Realm, context: Context, data: OrderedRealmCollection<FinanceModel>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<FinanceModel, FinancesRecyclerAdapter.ViewHolder>(context, data, autoUpdate) {
    private val HEADER = 1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        if (viewType == HEADER)
            return HeaderViewHolder(realm, context, inflater.inflate(R.layout.item_finances_header, parent, false))
        else
            return NormalViewHolder(inflater.inflate(R.layout.item_finances, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (position == data?.size) holder?.bindData(getItem(position - 1) as FinanceModel)
        else holder?.bindData(getItem(position) as FinanceModel)
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == data?.size) return HEADER
        return super.getItemViewType(position)
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(item: FinanceModel)
    }

    class NormalViewHolder(view: View) : FinancesRecyclerAdapter.ViewHolder(view) {
        override fun bindData(item: FinanceModel) {
            itemView.finance_description.text = item.description
            itemView.finance_date.text = DateTime.parse(item.dueDate.substring(0, 10)).toString(DateTimeFormat.forPattern("dd MMM ''yy"))
            itemView.finance_amount.text = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(item.chargeAmount.toFloat())
            if (DateTime.parse(item.dueDate.substring(0, 10)).isAfterNow) {
                itemView.finance_passed.visibility = View.GONE
                itemView.finance_upcoming.visibility = View.VISIBLE
            } else {
                itemView.finance_passed.visibility = View.VISIBLE
                itemView.finance_upcoming.visibility = View.GONE
            }
        }
    }

    class HeaderViewHolder(val realm: Realm, val ctx: Context, view: View) : FinancesRecyclerAdapter.ViewHolder(view) {
        override fun bindData(item: FinanceModel) {
            val data = realm.where(FinanceModel::class.java).findAll()
            val closestDate = data.map {
                DateTime.parse(it.dueDate.substring(0, 10))
            }.filter {
                it.isAfterNow
            }.sortedWith(DateTimeComparator.getInstance())
            itemView.total_amount.text = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(ctx.readPref(R.string.finance_charge, 0) as Int)
            if (closestDate.size != 0) {
                val upcomingBill = data.filter { DateTime.parse(it.dueDate.substring(0, 10)).isAfterNow }
                val totalBill = upcomingBill.sumBy { it.paymentAmount.toDouble().toInt() }
                itemView.total_amount.text = "Rp. ${NumberFormat.getNumberInstance(Locale.US).format(totalBill)}"
                val lengthFromToday = Days.daysBetween(closestDate[0], DateTime.now())
                itemView.next_charge.text = DateTime(closestDate[0]).toString(DateTimeFormat.forPattern("dd MMMM")) + """ (${lengthFromToday.days.toString().substring(1)} days)"""
            }else{
                itemView.total_amount.text = "Rp. 0,-"
                itemView.next_charge.text = "-"
            }
        }
    }
}