package com.directdev.portal.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.FinanceModel
import com.directdev.portal.model.JournalModel
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_finances.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.NumberFormat
import java.util.*

class FinancesRecyclerAdapter(val realm: Realm, context: Context, data: OrderedRealmCollection<FinanceModel>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<FinanceModel, FinancesRecyclerAdapter.ViewHolder>(context, data, autoUpdate) {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_finances, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindData(getItem(position) as FinanceModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(item: FinanceModel) {
            itemView.finance_description.text = item.description
            itemView.finance_date.text = "Due: " + DateTime.parse(item.dueDate.substring(0, 10)).toString(DateTimeFormat.forPattern("dd MMM ''yy"))
            itemView.finance_amount.text = "Rp. " + NumberFormat.getNumberInstance(Locale.US).format(item.chargeAmount.toFloat())
            if(DateTime.parse(item.dueDate.substring(0, 10)).isAfterNow){
                itemView.finance_passed.visibility = View.GONE
                itemView.finance_upcoming.visibility = View.VISIBLE
            }else{
                itemView.finance_passed.visibility = View.VISIBLE
                itemView.finance_upcoming.visibility = View.GONE
            }
        }
    }
}