package com.github.iojjj.survey.app

import com.github.iojjj.survey.networking.SurveyClient
import com.github.iojjj.survey.networking.retrofit.Interceptors
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named

/**
 * Module that provides debug specific dependencies.
 */
@Module
internal object BuildTypeModule {

    @JvmStatic
    @Provides
    @IntoSet
    @Interceptors
    fun provideOkHttpLogger(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @JvmStatic
    @Provides
    @Named("Local")
    fun provideSurveyClient(surveyClient: SurveyClient): SurveyClient {
        DebugSurveyClient.setDelegate(surveyClient)
        return DebugSurveyClient
    }
}