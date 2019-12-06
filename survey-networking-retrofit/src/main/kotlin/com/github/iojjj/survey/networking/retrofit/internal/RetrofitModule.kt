package com.github.iojjj.survey.networking.retrofit.internal

import com.github.iojjj.survey.networking.SurveyClient
import com.github.iojjj.survey.networking.retrofit.BuildConfig
import com.github.iojjj.survey.networking.retrofit.Interceptors
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Internal Retrofit specific dependencies
 */
@Module
internal interface RetrofitModule {

    @Binds
    fun bindsSurveyClient(client: SurveyClientImpl): SurveyClient

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideOkHttpClient(@Interceptors interceptors: @JvmSuppressWildcards Set<Interceptor>): OkHttpClient {
            return OkHttpClient.Builder()
                .apply {
                    interceptors.forEach { addInterceptor(it) }
                }
                .build()
        }

        @JvmStatic
        @Provides
        fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .validateEagerly(true)
        }

        @JvmStatic
        @Provides
        fun provideQuestionsEndpoints(retrofitBuilder: Retrofit.Builder): QuestionsEndpoint {
            return retrofitBuilder.build().create(QuestionsEndpoint::class.java)
        }
    }
}