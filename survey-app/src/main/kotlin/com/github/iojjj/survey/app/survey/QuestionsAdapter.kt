package com.github.iojjj.survey.app.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.github.iojjj.survey.app.R
import com.github.iojjj.survey.core.lists.AsyncListDifferFactory
import javax.inject.Inject

/**
 * Implementation of adapter for questions
 * @property _listDiffer [AsyncListDiffer] that handles all list changes.
 */
class QuestionsAdapter @Inject internal constructor(factory: AsyncListDifferFactory<QuestionWithAnswer>) :
    RecyclerView.Adapter<QuestionViewHolder>() {

    private val _listDiffer = factory.create(this)

    fun setQuestions(questions: List<QuestionWithAnswer>) {
        _listDiffer.submitList(questions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.survey_list_item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = _listDiffer.currentList[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int = _listDiffer.currentList.size

}