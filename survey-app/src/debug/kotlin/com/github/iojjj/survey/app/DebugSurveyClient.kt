package com.github.iojjj.survey.app

import com.github.iojjj.survey.networking.Question
import com.github.iojjj.survey.networking.SurveyClient
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Implementation of [SurveyClient] that allows intercept real calls.
 *
 * User in automation tests.
 */
@Suppress("ObjectPropertyName")
object DebugSurveyClient : SurveyClient {

    private lateinit var _delegate: SurveyClient
    private var _returnQuestions: List<Question>? = null
    private var _returnQuestionsError: Throwable? = null
    private var _returnSubmitAnswer: Boolean = false
    private var _returnSubmitAnswerError: Throwable? = null

    /**
     * Set actual delegate.
     * @param delegate delegate that will be invoked if requests are not intercepted.
     */
    fun setDelegate(delegate: SurveyClient) {
        _delegate = delegate
    }

    /**
     * Setup interception for questions.
     * @param value questions that should be returned
     * @param error error that should be thrown
     */
    fun nextQuestions(value: List<Question>? = null, error: Throwable? = null) {
        _returnQuestions = value
        _returnQuestionsError = error
    }

    /**
     * Setup interception for submitting answer.
     * @param intercept flag indicates to execute successful call
     * @param error error that should be thrown
     */
    fun nextSubmitAnswer(intercept: Boolean = false, error: Throwable? = null) {
        _returnSubmitAnswer = intercept
        _returnSubmitAnswerError = error
    }

    override fun getQuestions(): Single<List<Question>> {
        return Single.defer {
            val value = _returnQuestions
            val error = _returnQuestionsError
            when {
                error != null -> Single.error(error)
                value != null -> Single.just(value)
                else -> _delegate.getQuestions()
            }
        }
    }

    override fun submitAnswer(questionId: Long, answer: String): Completable {
        return Completable.defer {
            val intercept = _returnSubmitAnswer
            val error = _returnSubmitAnswerError
            when {
                error != null -> Completable.error(error)
                intercept -> Completable.complete()
                else -> _delegate.submitAnswer(questionId, answer)
            }
        }
    }
}