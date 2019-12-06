package com.github.iojjj.survey.app

import com.github.iojjj.survey.core.di.FragmentScope
import com.github.iojjj.survey.app.main.MainFragment
import com.github.iojjj.survey.app.survey.SurveyFragment
import com.github.iojjj.survey.app.survey.SurveyModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SurveyBindings {

    @FragmentScope
    @ContributesAndroidInjector
    fun contributeMainFragmentInjector(): MainFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SurveyModule::class])
    fun contributeSurveyFragmentInjector(): SurveyFragment
}