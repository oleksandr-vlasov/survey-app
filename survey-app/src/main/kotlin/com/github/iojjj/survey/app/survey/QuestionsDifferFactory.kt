package com.github.iojjj.survey.app.survey

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.github.iojjj.survey.core.lists.AsyncListDifferFactory
import javax.inject.Inject

/**
 * Implementation of [AsyncListDifferFactory] for [QuestionWithAnswer].
 */
class QuestionsDifferFactory @Inject internal constructor() : AsyncListDifferFactory<QuestionWithAnswer> {

    override fun create(adapter: RecyclerView.Adapter<*>): AsyncListDiffer<QuestionWithAnswer> {
        return AsyncListDiffer(adapter, DiffUtil)
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<QuestionWithAnswer>() {

        override fun areItemsTheSame(oldItem: QuestionWithAnswer, newItem: QuestionWithAnswer): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: QuestionWithAnswer, newItem: QuestionWithAnswer): Boolean = oldItem == newItem
    }
}