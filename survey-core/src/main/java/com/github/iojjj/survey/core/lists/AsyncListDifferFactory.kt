package com.github.iojjj.survey.core.lists

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView

/**
 * Factory that creates and binds [AsyncListDiffer] to provided adapter.
 * @param T type of entities in adapter
 */
interface AsyncListDifferFactory<T> {

    /**
     * Create a new [AsyncListDiffer].
     * @param adapter Adapter to bind to
     * @return a newly created instance of `AsyncListDiffer`
     */
    fun create(adapter: RecyclerView.Adapter<*>): AsyncListDiffer<T>
}