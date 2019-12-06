package com.github.iojjj.survey.app.survey

import androidx.lifecycle.ViewModel
import com.github.iojjj.survey.core.lists.AsyncListDifferFactory
import com.github.iojjj.survey.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface SurveyModule {

    @Binds
    fun bindQuestionsDifferFactory(factory: QuestionsDifferFactory): AsyncListDifferFactory<QuestionWithAnswer>

    @Binds
    @IntoMap
    @ViewModelKey(SurveyViewModel::class)
    fun bindSurveyViewModel(viewModel: SurveyViewModel): ViewModel
}