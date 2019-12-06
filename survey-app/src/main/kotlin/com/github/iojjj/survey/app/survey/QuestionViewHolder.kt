package com.github.iojjj.survey.app.survey

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewHolder for a single question.
 * @property _questionText Question text.
 */
class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val _questionText: TextView = view as TextView

    fun bind(question: QuestionWithAnswer) {
        _questionText.text = question.question
    }

}