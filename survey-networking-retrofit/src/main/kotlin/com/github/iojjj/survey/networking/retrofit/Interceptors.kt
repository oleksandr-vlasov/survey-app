package com.github.iojjj.survey.networking.retrofit

import okhttp3.OkHttpClient
import javax.inject.Qualifier

/**
 * Interceptors that should be added to [OkHttpClient].
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class Interceptors