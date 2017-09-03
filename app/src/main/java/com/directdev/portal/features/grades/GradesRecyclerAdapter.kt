package com.directdev.portal.features.grades

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.models.CreditModel
import com.directdev.portal.models.ScoreModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_grades.view.*
import kotlinx.android.synthetic.main.item_grades_header.view.*

/**-------------------------------------------------------------------------------------------------
 *
 * Adapter for grades, this generates the list of card showing the grades of every semester on
 * the grades fragment. Includes a header for showing GPA.
 *
 *------------------------------------------------------------------------------------------------*/

class GradesRecyclerAdapter(
        var grades: MutableList<RealmResults<ScoreModel>> = mutableListOf(),
        var credit: CreditModel = CreditModel()) :
        RecyclerView.Adapter<GradesRecyclerAdapter.ViewHolder>() {

    private val HEADER = 1

    // +1 because of the added header
    override fun getItemCount(): Int {
        return grades.size + 1
    }

    // returns view type of header when position is 0
    override fun getItemViewType(position: Int) =
            if (position == 0) HEADER
            else super.getItemViewType(position)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // on position 0, bind the first data into the viewholder
        // on the next position, bind the data normally, starting from data 0
        if (grades.isEmpty()) return
        if (position == 0) holder.bindData(grades[position], credit)
        else holder.bindData(grades[position - 1], credit)
    }

    // Return the correct viewHolder based on the viewtype return by getItemViewType
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return if (viewType == HEADER)
            HeaderViewHolder(inflater.inflate(R.layout.item_grades_header, parent, false))
        else
            NormalViewHolder(inflater.inflate(R.layout.item_grades, parent, false))
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(score: RealmResults<ScoreModel>, credit: CreditModel)
    }

    // TODO: REFACTOR | Recent binusmaya changes blows up the when statement, better alternative needed
    private class NormalViewHolder(view: View) : ViewHolder(view) {
        val gone = View.GONE
        val visible = View.VISIBLE
        override fun bindData(score: RealmResults<ScoreModel>, credit: CreditModel) {
            itemView.item_grades_cardview.visibility = visible
            if (score.isEmpty()) {
                itemView.item_grades_cardview.visibility = gone
                return
            }
            itemView.course_name.text = score[0].courseName
            itemView.course_grades.text = score[0].courseGradeTotal
            itemView.mid.visibility = gone
            itemView.fin.visibility = gone
            itemView.assignment.visibility = gone
            itemView.laboratory_assignment.visibility = gone
            itemView.laboratory_quiz.visibility = gone
            itemView.laboratory_fin.visibility = gone
            itemView.laboratory_project.visibility = gone
            score.forEach {
                when (it.scoreType) {
                    "ASSIGNMENT" -> {
                        itemView.assignment.visibility = visible
                        itemView.assignment.text = "Assignment	: " + it.score
                    }
                    "MID EXAM" -> {
                        itemView.mid.visibility = visible
                        itemView.mid.text = "Mid Exam    	: " + it.score
                    }
                    "FINAL EXAM" -> {
                        itemView.fin.visibility = visible
                        itemView.fin.text = "Final Exam  	: " + it.score
                    }
                    "LABORATORY" -> {
                        itemView.laboratory_assignment.visibility = visible
                        itemView.laboratory_assignment.text = "Laboratory  	: " + it.score
                    }
                    "THEORY: Assignment" -> {
                        itemView.assignment.visibility = visible
                        itemView.assignment.text = "Assignment	: " + it.score
                    }
                    "THEORY: Mid Exam" -> {
                        itemView.mid.visibility = visible
                        itemView.mid.text = "Mid Exam    	: " + it.score
                    }
                    "THEORY: Final Exam" -> {
                        itemView.fin.visibility = visible
                        itemView.fin.text = "Final Exam  	: " + it.score
                    }
                    "LAB: Quiz" -> {
                        itemView.laboratory_quiz.visibility = visible
                        itemView.laboratory_quiz.text = "Lab Quiz	: " + it.score
                    }

                    "LAB: Assignment" -> {
                        itemView.laboratory_assignment.visibility = visible
                        itemView.laboratory_assignment.text = "Lab assignment  	: " + it.score
                    }

                    "LAB: Project" -> {
                        itemView.laboratory_project.visibility = visible
                        itemView.laboratory_project.text = "Lab project 	: " + it.score
                    }

                    "LAB: Final Exam" -> {
                        itemView.laboratory_fin.visibility = visible
                        itemView.laboratory_fin.text = "Lab Final   	 : " + it.score
                    }
                    "THEORY: Final - Multipaper 1" -> {
                        itemView.final_multipaper_1.visibility = visible
                        itemView.final_multipaper_1.text = "Final Multipaper I   	 : " + it.score
                    }
                    "THEORY: Final - Multipaper 2" -> {
                        itemView.final_multipaper_2.visibility = visible
                        itemView.final_multipaper_2.text = "Final Multipaper II   	 : " + it.score
                    }
                }
            }
            val bgColor = when (score[0].courseGradeTotal[0]) {
                'A' -> "#1565c0"
                'B' -> "#1b5e20"
                'C' -> "#bf360c"
                'D' -> "#b71c1c"
                'E' -> "#212121"
                else -> "#795548"
            }
            itemView.item_grades_cardview.setCardBackgroundColor(Color.parseColor(bgColor))
        }
    }

    private class HeaderViewHolder(view: View) : ViewHolder(view) {
        override fun bindData(score: RealmResults<ScoreModel>, data: CreditModel) {
            itemView.totalCreditCount.text = "${data.scuFinished} SCU"
            itemView.cumulativeGpaCount.text = data.gpaCummulative
            itemView.semesterGpaCount.text = if (data.gpaCurrent == "0.00") "N/A" else data.gpaCurrent
        }
    }

    fun updateData(grades: List<RealmResults<ScoreModel>>, credit: CreditModel) {
        this.grades.clear()
        this.grades.addAll(grades)
        this.credit = credit
        notifyDataSetChanged()
    }
}