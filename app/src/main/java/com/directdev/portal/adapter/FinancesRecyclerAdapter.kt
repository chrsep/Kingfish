package com.directdev.portal.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.FinanceModel
import com.directdev.portal.utils.formatToRupiah
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_finances.view.*
import kotlinx.android.synthetic.main.item_finances_header.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat

/**-------------------------------------------------------------------------------------------------
 *
 * Adapter for list of bills in finance fragment. It includes a header that shows the total number
 * of incoming bills.
 *
 *------------------------------------------------------------------------------------------------*/
// TODO: REFACTOR | This list is reversed, and i forgot why, further investigation needed
// This recyclerView is in reversed order, so we put the header (The one that shows unpaid bill) at
// the end of the list to make it show on top
class FinancesRecyclerAdapter(
        val realm: Realm,
        data: OrderedRealmCollection<FinanceModel>?,
        autoUpdate: Boolean) :
        RealmRecyclerViewAdapter<FinanceModel, FinancesRecyclerAdapter.ViewHolder>(data, autoUpdate) {
    private val HEADER = 1

    override fun getItemCount() = super.getItemCount() + 1

    // Normally for header, position==0 will be used (So that it shows on top), since this is
    // reversed, we will want to put the header on the bottom.
    override fun getItemViewType(position: Int) =
            if (position == data?.size) HEADER
            else super.getItemViewType(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            if (viewType == HEADER)
                HeaderViewHolder(realm, LayoutInflater.from(parent.context).inflate(R.layout.item_finances_header, parent, false))
            else
                NormalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_finances, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (position == data?.size)
            holder?.bindData(getItem(position - 1) as FinanceModel)
        else
            holder?.bindData(getItem(position) as FinanceModel)
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(item: FinanceModel)
    }

    private class NormalViewHolder(view: View) : FinancesRecyclerAdapter.ViewHolder(view) {
        override fun bindData(item: FinanceModel) {
            val date = DateTime
                    .parse(item.dueDate.substring(0, 10))
                    .toString(DateTimeFormat.forPattern("dd MMM ''yy"))
            val amount = item.chargeAmount.formatToRupiah()
            itemView.finance_description.text = item.description
            itemView.finance_date.text = date
            itemView.finance_amount.text = amount
            if (DateTime.parse(item.dueDate.substring(0, 10)).isAfterNow) {
                itemView.finance_passed.visibility = View.GONE
                itemView.finance_upcoming.visibility = View.VISIBLE
            } else {
                itemView.finance_passed.visibility = View.VISIBLE
                itemView.finance_upcoming.visibility = View.GONE
            }
        }
    }

    private class HeaderViewHolder(val realm: Realm, view: View) : FinancesRecyclerAdapter.ViewHolder(view) {
        override fun bindData(item: FinanceModel) {
            val totalBillText: String
            val nextChargeText: String
            val data = realm.where(FinanceModel::class.java).findAll()
            val closestDate = data
                    .map { DateTime.parse(it.dueDate.substring(0, 10)) }
                    .filter { it.isAfterNow }
                    .sortedWith(DateTimeComparator.getInstance())
            if (closestDate.isNotEmpty()) {
                val nextChargeDate = closestDate[0].toString("dd MMMM")
                val daysCount = Days
                        .daysBetween(closestDate[0], DateTime.now())
                        .days
                        .toString()
                        .substring(1)
                val totalBill = data
                        .filter { DateTime.parse(it.dueDate.substring(0, 10)).isAfterNow }
                        .sumBy { it.chargeAmount.toDouble().toInt() }
                totalBillText = "Rp. $totalBill"
                nextChargeText = "$nextChargeDate ($daysCount days)"
            } else {
                totalBillText = "Rp. 0,-"
                nextChargeText = "-"
            }
            itemView.total_amount.text = totalBillText
            itemView.next_charge.text = nextChargeText
        }
    }
}