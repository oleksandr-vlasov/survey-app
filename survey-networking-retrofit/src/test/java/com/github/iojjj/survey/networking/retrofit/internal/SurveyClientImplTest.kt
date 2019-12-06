package com.github.iojjj.survey.networking.retrofit.internal

import com.github.iojjj.survey.networking.Answer
import com.github.iojjj.survey.networking.Question
import com.github.iojjj.survey.testing.RxTestRule
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SurveyClientImplTest {

    @Mock
    private lateinit var questionsEndpoint: QuestionsEndpoint
    @Mock
    private lateinit var questions: List<Question>
    @Mock
    private lateinit var error: Throwable

    private val answerToVerify = Answer(1L, "Answer")
    private lateinit var getQuestionsResponse: Single<List<Question>>
    private lateinit var getQuestionsError: Single<List<Question>>
    private val submitAnswerResponse = Completable.complete()
    private lateinit var submitAnswerError: Completable

    private lateinit var classToTest: SurveyClientImpl

    @Before
    fun setUp() {
        classToTest = SurveyClientImpl(questionsEndpoint)
        getQuestionsResponse = Single.just(questions)
        getQuestionsError = Single.error<List<Question>>(error)
        submitAnswerError = Completable.error(error)

        `when`(questionsEndpoint.getQuestions()).thenReturn(getQuestionsResponse)
        `when`(questionsEndpoint.submitAnswer(answerToVerify)).thenReturn(submitAnswerResponse)
    }

    @Test
    fun getQuestions_whenCalled_thenCallsQuestionsEndpoint() {
        classToTest.getQuestions().test()
        verify(questionsEndpoint).getQuestions()
        verifyNoMoreInteractions(questionsEndpoint)
    }

    @Test
    fun getQuestions_whenNetworkCallReturnsResponse_thenPassesResponseToCaller() {
        val observer = classToTest.getQuestions().test()
        observer.assertValue(questions)
    }

    @Test
    fun getQuestions_whenNetworkCallThrowsException_thenPassesExceptionToCaller() {
        `when`(questionsEndpoint.getQuestions()).thenReturn(getQuestionsError)
        val observer = classToTest.getQuestions().test()
        observer.assertError(error)
    }

    @Test
    fun submitAnswer_whenCalled_thenCallsQuestionsEndpoint() {
        classToTest.submitAnswer(answerToVerify.id, answerToVerify.text).test()
        verify(questionsEndpoint).submitAnswer(answerToVerify)
        verifyNoMoreInteractions(questionsEndpoint)
    }

    @Test
    fun submitAnswer_whenNetworkCallReturnsResponse_thenPassesResponseToCaller() {
        val observer = classToTest.submitAnswer(answerToVerify.id, answerToVerify.text).test()
        observer.assertComplete()
    }

    @Test
    fun submitAnswer_whenNetworkCallThrowsException_thenPassesExceptionToCaller() {
        `when`(questionsEndpoint.submitAnswer(answerToVerify)).thenReturn(submitAnswerError)
        val observer = classToTest.submitAnswer(answerToVerify.id, answerToVerify.text).test()
        observer.assertError(error)
    }

    companion object {

        @JvmStatic
        @get:ClassRule
        val RX_RULE = RxTestRule()
    }
}