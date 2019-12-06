package com.github.iojjj.survey.app

import com.github.iojjj.survey.networking.SurveyClient
import dagger.Binds
import dagger.Module
import javax.inject.Named

@Module
internal interface BuildTypeModule {

    // just bind to local
    @Binds
    @Named("Local")
    fun bindSurveyClient(surveyClient: SurveyClient): SurveyClient
}