package com.github.iojjj.survey.core.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import dagger.android.support.DaggerFragment

/**
 * Base fragment implementation that just inflates specified layout.
 * @property _layoutId Layout resource ID
 */
abstract class BaseFragment protected constructor(@LayoutRes private val _layoutId: Int) : DaggerFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(_layoutId, container, false)
    }
}