package com.github.iojjj.survey.app

import com.github.iojjj.survey.networking.retrofit.Interceptors
import dagger.Module
import dagger.multibindings.Multibinds
import okhttp3.Interceptor

@Module
interface SurveyApplicationModule {

    /**
     * Provide an empty set of interceptors if there are no interceptors provided by user.
     */
    @Interceptors
    @Multibinds
    fun interceptors(): Set<Interceptor>
}