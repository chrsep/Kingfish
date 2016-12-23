package com.directdev.portal.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.directdev.portal.R
import com.directdev.portal.model.ScoreModel
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_grades.view.*


class GradesRecyclerAdapter(val realm: Realm, val data: List<RealmResults<ScoreModel>>) : RecyclerView.Adapter<GradesRecyclerAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return GradesRecyclerAdapter.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_grades, parent, false))
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gone = View.GONE
        val visible = View.VISIBLE
        fun bindData(score: RealmResults<ScoreModel>) {
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
                when(it.scoreType){
                    "ASSIGNMENT" ->{
                        itemView.assignment.visibility = visible
                        itemView.assignment.text = "Assignment	: " +it.score
                    }
                    "MID EXAM"   ->{
                        itemView.mid.visibility = visible
                        itemView.mid.text = "Mid Exam    	: " + it.score
                    }
                    "FINAL EXAM" ->{
                        itemView.fin.visibility = visible
                        itemView.fin.text = "Final Exam  	: " +it.score
                    }
                    "LABORATORY" ->{
                        itemView.laboratory_assignment.visibility = visible
                        itemView.laboratory_assignment.text = "Laboratory  	: " + it.score
                    }
                    "THEORY: Assignment" ->{
                        itemView.assignment.visibility = visible
                        itemView.assignment.text = "Assignment	: " +it.score
                    }
                    "THEORY: Mid Exam"   ->{
                        itemView.mid.visibility = visible
                        itemView.mid.text = "Mid Exam    	: " + it.score
                    }
                    "THEORY: Final Exam" ->{
                        itemView.fin.visibility = visible
                        itemView.fin.text = "Final Exam  	: " +it.score
                    }
                    "LAB: Quiz" ->{
                        itemView.laboratory_quiz.visibility = visible
                        itemView.laboratory_quiz.text = "Lab Quiz	: " + it.score
                    }

                    "LAB: Assignment" ->{
                        itemView.laboratory_assignment.visibility = visible
                        itemView.laboratory_assignment.text = "Lab assignment  	: " + it.score
                    }

                    "LAB: Project" ->{
                        itemView.laboratory_project.visibility = visible
                        itemView.laboratory_project.text = "Lab project 	: " + it.score
                    }

                    "LAB: Final Exam" ->{
                        itemView.laboratory_fin.visibility = visible
                        itemView.laboratory_fin.text = "Lab Final   	 : " + it.score
                    }
                }
            }
            when (score[0].courseGradeTotal[0]) {
                'A' -> itemView.item_grades_cardview.setCardBackgroundColor(Color.parseColor("#1565c0"))
                'B' -> itemView.item_grades_cardview.setCardBackgroundColor(Color.parseColor("#1b5e20"))
                'C' -> itemView.item_grades_cardview.setCardBackgroundColor(Color.parseColor("#bf360c"))
                'D' -> itemView.item_grades_cardview.setCardBackgroundColor(Color.parseColor("#b71c1c"))
                'E' -> itemView.item_grades_cardview.setCardBackgroundColor(Color.BLACK)
                else -> itemView.item_grades_cardview.setCardBackgroundColor(Color.parseColor("#795548"))
            }
        }
    }
}