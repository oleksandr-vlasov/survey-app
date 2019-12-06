package com.github.iojjj.survey.networking.retrofit.internal

import com.github.iojjj.survey.networking.Answer
import com.github.iojjj.survey.networking.Question
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface QuestionsEndpoint {

    /**
     * Get list of questions.
     * @return `Single` that emits list of questions
     */
    @GET("questions")
    fun getQuestions(): Single<List<Question>>

    /**
     * Submit answer.
     * @param answer Answer answer
     * @return `Completable` that completes or throws an error
     */
    @POST("question/submit")
    fun submitAnswer(@Body answer: Answer): Completable
}