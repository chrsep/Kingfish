package com.directdev.portal.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.ScoreModel
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_grades.view.*


class GradesRecyclerAdapter(
        val realm: Realm,
        val data: List<RealmResults<ScoreModel>>) :
        RecyclerView.Adapter<GradesRecyclerAdapter.ViewHolder>() {

    private val HEADER = 1

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) holder.bindData(data[position])
        else holder.bindData(data[position - 1])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        if (viewType == HEADER)
            return HeaderViewHolder(inflater.inflate(R.layout.item_grades_header, parent, false))
        else
            return NormalViewHolder(inflater.inflate(R.layout.item_grades, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return HEADER
        return super.getItemViewType(position)
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bindData(score: RealmResults<ScoreModel>)
    }

    private class NormalViewHolder(view: View) : ViewHolder(view) {
        val gone = View.GONE
        val visible = View.VISIBLE
        override fun bindData(score: RealmResults<ScoreModel>) {
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
        override fun bindData(score: RealmResults<ScoreModel>) {

        }
    }
}