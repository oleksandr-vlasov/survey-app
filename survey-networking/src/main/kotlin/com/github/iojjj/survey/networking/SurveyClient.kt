package com.github.iojjj.survey.networking

import io.reactivex.Completable
import io.reactivex.Single

/**
 * Network client that allows to perform survey.
 */
interface SurveyClient {

    /**
     * Get list of questions.
     * @return `Single` that emits list of questions
     */
    fun getQuestions(): Single<List<Question>>

    /**
     * Submit answer.
     * @param answer Answer answer
     * @return `Completable` that completes or throws an error
     */
    fun submitAnswer(questionId: Long, answer: String): Completable
}