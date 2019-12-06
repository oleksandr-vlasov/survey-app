package com.github.iojjj.survey.app.survey

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.iojjj.survey.networking.Question
import com.github.iojjj.survey.networking.SurveyClient
import com.github.iojjj.survey.testing.RxDeferred
import com.github.iojjj.survey.testing.RxTestRule
import com.github.iojjj.survey.testing.test
import com.github.iojjj.survey.app.survey.SurveyCommand.ReloadQuestions
import com.github.iojjj.survey.app.survey.SurveyCommand.SubmitAnswer
import com.github.iojjj.survey.app.survey.SurveyViewState.*
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException

@RunWith(RobolectricTestRunner::class)
class SurveyViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()
    @get:Rule
    val rxTestRule = RxTestRule()               // Robolectric doesn't invoke ClassRule

    @Mock
    private lateinit var surveyClient: SurveyClient

    private val submitAnswerCommand = SubmitAnswer(1L, "Answer")
    private val singleQuestion = Question(1L, "Question")
    private val singleQuestionWithAnswer =
        QuestionWithAnswer(singleQuestion.id, singleQuestion.question, "")
    private val questionsDeferred = RxDeferred<List<Question>>()

    private lateinit var classToTest: SurveyViewModel

    @Before
    fun setUp() {
        questionsDeferred.next(value = emptyList())
        `when`(surveyClient.getQuestions()).thenReturn(questionsDeferred.single())
        `when`(surveyClient.submitAnswer(anyLong(), anyString())).thenReturn(Completable.complete())

        classToTest = SurveyViewModel(surveyClient)
    }

    @After
    fun tearDown() {
        classToTest.cleanUp()
    }

    @Test
    fun onCreate_whenInvoked_thenLoadsQuestions() {
        questionsDeferred.next(value = listOf(singleQuestion))
        verify(surveyClient).getQuestions()
        verifyNoMoreInteractions(surveyClient)
    }

    @Test
    fun onCreate_whenLoadsQuestionsWithoutErrors_thenUpdatesViewState() {
        questionsDeferred.next(value = listOf(singleQuestion))
        val observer = classToTest.state.test()
        classToTest.onStart()

        assertEquals(4, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(QuestionsUpdated(listOf(singleQuestionWithAnswer)), observer.observedValues[2])
        assertEquals(QuestionsAnswered(0), observer.observedValues[3])
    }

    @Test
    fun onCreate_whenLoadsQuestionsWithGeneralError_thenSetsFailureState() {
        questionsDeferred.next(error = mock(Throwable::class.java))
        val observer = classToTest.state.test()
        classToTest.onStart()

        assertEquals(4, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(QuestionsUpdated(emptyList()), observer.observedValues[2])
        assertEquals(Failure.GeneralError(ReloadQuestions), observer.observedValues[3])
    }

    @Test
    fun onCreate_whenLoadsQuestionsWithNetworkError_thenSetsFailureState() {
        questionsDeferred.next(error = mock(HttpException::class.java))
        val observer = classToTest.state.test()
        classToTest.onStart()

        assertEquals(4, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(QuestionsUpdated(emptyList()), observer.observedValues[2])
        assertEquals(Failure.NetworkError(ReloadQuestions), observer.observedValues[3])
    }

    @Test
    fun execute_whenReloadQuestions_thenLoadsQuestionsFromServer() {
        questionsDeferred.next(value = listOf(singleQuestion))
        verify(surveyClient).getQuestions()
        verifyNoMoreInteractions(surveyClient)
    }

    @Test
    fun execute_whenReloadQuestionsWithoutErrors_thenUpdatesViewState() {
        questionsDeferred.next(value = listOf(singleQuestion))
        val observer = classToTest.state.test()
        classToTest._questionsLoaded = true
        classToTest.execute(ReloadQuestions)

        assertEquals(3, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(QuestionsUpdated(listOf(singleQuestionWithAnswer)), observer.observedValues[2])
    }

    @Test
    fun execute_whenReloadQuestions_thenProperlyMapsResponse() {
        questionsDeferred.next(
            value = listOf(
                Question(1L, "Question 1"),
                Question(2L, "Question 2"),
                Question(3L, "Question 3")
            )
        )
        classToTest.execute(ReloadQuestions)
        assertEquals(QuestionWithAnswer(1L, "Question 1", ""), classToTest.getQuestion(0))
        assertEquals(QuestionWithAnswer(2L, "Question 2", ""), classToTest.getQuestion(1))
        assertEquals(QuestionWithAnswer(3L, "Question 3", ""), classToTest.getQuestion(2))
    }

    @Test
    fun execute_whenSubmitAnswer_thenSubmitsAnswerToServer() {
        // getQuestions always called in constructor
        verify(surveyClient).getQuestions()

        classToTest.execute(submitAnswerCommand)
        verify(surveyClient).submitAnswer(submitAnswerCommand.questionId, submitAnswerCommand.answer)
        verifyNoMoreInteractions(surveyClient)
    }

    @Test
    fun execute_whenSubmitAnswerWithoutErrors_thenUpdatesViewState() {
        // getQuestions always called in constructor
        verify(surveyClient).getQuestions()

        val observer = classToTest.state.test()
        classToTest.execute(submitAnswerCommand)
        assertEquals(3, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(AnswerSubmitted, observer.observedValues[2])
    }

    @Test
    fun execute_whenSubmitAnswerWithGeneralError_thenSetsFailureState() {
        // getQuestions always called in constructor
        verify(surveyClient).getQuestions()

        `when`(
            surveyClient.submitAnswer(
                submitAnswerCommand.questionId,
                submitAnswerCommand.answer
            )
        ).thenReturn(Completable.error(mock(Throwable::class.java)))
        val observer = classToTest.state.test()
        classToTest.execute(submitAnswerCommand)
        assertEquals(3, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(Failure.GeneralError(submitAnswerCommand), observer.observedValues[2])
    }

    @Test
    fun execute_whenSubmitAnswerWithNetworkError_thenSetsFailureState() {
        // getQuestions always called in constructor
        verify(surveyClient).getQuestions()

        `when`(
            surveyClient.submitAnswer(
                submitAnswerCommand.questionId,
                submitAnswerCommand.answer
            )
        ).thenReturn(Completable.error(mock(HttpException::class.java)))
        val observer = classToTest.state.test()
        classToTest.execute(submitAnswerCommand)
        assertEquals(3, observer.observedValues.size)
        assertEquals(Loading(true), observer.observedValues[0])
        assertEquals(Loading(false), observer.observedValues[1])
        assertEquals(Failure.NetworkError(submitAnswerCommand), observer.observedValues[2])
    }

    @Test
    fun execute_whenSubmitAnswer_thenQuestionsListUpdated() {
        questionsDeferred.next(
            value = listOf(
                Question(1L, "Question 1"),
                Question(2L, "Question 2"),
                Question(3L, "Question 3")
            )
        )
        classToTest.execute(ReloadQuestions)

        classToTest.execute(SubmitAnswer(1L, "Answer 1"))
        classToTest.execute(SubmitAnswer(2L, "Answer 2"))
        classToTest.execute(SubmitAnswer(3L, "Answer 3"))
        assertEquals(QuestionWithAnswer(1L, "Question 1", "Answer 1"), classToTest.getQuestion(0))
        assertEquals(QuestionWithAnswer(2L, "Question 2", "Answer 2"), classToTest.getQuestion(1))
        assertEquals(QuestionWithAnswer(3L, "Question 3", "Answer 3"), classToTest.getQuestion(2))
    }

    @Test
    fun getQuestion_whenQuestionsAreEmpty_thenReturnsNull() {
        assertNull(classToTest.getQuestion(0))
    }

    @Test
    fun getQuestion_whenPositionOutOfBounds_thenReturnsNull() {
        // load questions
        questionsDeferred.next(value = listOf(singleQuestion))
        classToTest.execute(ReloadQuestions)

        assertNull(classToTest.getQuestion(-1))
        assertNull(classToTest.getQuestion(1))
    }

    @Test
    fun getQuestion_whenPositionIsCorrect_thenReturnsQuestion() {
        // load questions
        questionsDeferred.next(value = listOf(singleQuestion))
        classToTest.execute(ReloadQuestions)

        assertEquals(singleQuestionWithAnswer, classToTest.getQuestion(0))
    }
}