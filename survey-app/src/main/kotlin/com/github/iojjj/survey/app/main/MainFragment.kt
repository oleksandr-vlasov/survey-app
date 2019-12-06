package com.github.iojjj.survey.app.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.github.iojjj.survey.app.R
import com.github.iojjj.survey.core.mvvm.BaseFragment
import kotlinx.android.synthetic.main.survey_fragment_main.*

/**
 * Implementation of Main screen.
 */
class MainFragment : BaseFragment(R.layout.survey_fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        start.setOnClickListener { findNavController().navigate(R.id.goToSurvey) }
    }
}