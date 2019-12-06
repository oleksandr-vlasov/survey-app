package com.github.iojjj.survey.app

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class SurveyApplication : DaggerApplication() {

    private val _component: SurveyComponent by lazy {
        DaggerSurveyComponent.builder()
            .withApplication(this)
            .build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = _component

}