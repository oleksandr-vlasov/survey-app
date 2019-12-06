package com.github.iojjj.survey.networking.retrofit.internal

import com.github.iojjj.survey.networking.Answer
import com.github.iojjj.survey.networking.Question
import com.github.iojjj.survey.networking.SurveyClient
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Retrofit implementation of [SurveyClient].
 * @property _questionsEndpoint Retrofit's client for Questions endpoint
 */
internal class SurveyClientImpl @Inject constructor(private val _questionsEndpoint: QuestionsEndpoint) : SurveyClient {

    override fun getQuestions(): Single<List<Question>> = _questionsEndpoint.getQuestions()
        .subscribeOn(Schedulers.io())

    override fun submitAnswer(questionId: Long, answer: String): Completable {
        val answerBody = Answer(questionId, answer)
        return _questionsEndpoint.submitAnswer(answerBody)
            .subscribeOn(Schedulers.io())
    }

}