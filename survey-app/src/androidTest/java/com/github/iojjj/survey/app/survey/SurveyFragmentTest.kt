package com.github.iojjj.survey.app.survey

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.iojjj.survey.app.DebugSurveyClient
import com.github.iojjj.survey.app.R
import com.github.iojjj.survey.networking.Question
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class SurveyFragmentTest {

    private val networkError = HttpException(Response.error<List<Question>>(400, "".toResponseBody()))

    @After
    fun tearDown() {
        // clear interception
        DebugSurveyClient.nextQuestions()
        DebugSurveyClient.nextSubmitAnswer()
    }

    @Test
    fun whenGeneralErrorOccurredDuringQuestionsLoading_thenShowsEmptyViewAndSnackbarWithError() {
        DebugSurveyClient.nextQuestions(error = Throwable())
        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        checkLoadQuestionsWithError(R.string.survey_error_general)
    }

    @Test
    fun whenNetworkErrorOccurredDuringQuestionsLoading_thenShowsEmptyViewAndSnackbarWithError() {
        DebugSurveyClient.nextQuestions(error = networkError)
        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        checkLoadQuestionsWithError(R.string.survey_error_network)
    }

    @Test
    fun whenLoadedNoQuestions_thenShowsEmptyView() {
        DebugSurveyClient.nextQuestions(value = emptyList())
        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        checkLoadQuestionsEmpty()
    }

    @Test
    fun whenLoadedSingleQuestion_thenPrevNextButtonsDisabled() {
        DebugSurveyClient.nextQuestions(value = listOf(Question(1L, "Question")))
        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        checkVisibleButDisabled(R.id.previousButton, R.id.nextButton)
    }

    @Test
    fun whenLoadedQuestions_thenDisplaysScreenWithFirstSelectedQuestion() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.toolbar))
            .check(
                matches(
                    hasDescendant(
                        withText(getExpectedTitle(1, questions.size))
                    )
                )
            )
        onView(withId(R.id.answeredQuestions))
            .check(
                matches(
                    withText(getExpectedAnsweredQuestions(0))
                )
            )
        onView(withId(R.id.viewPager))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        hasDescendant(
                            allOf(
                                isCompletelyDisplayed(),
                                withText(questions[0].question)
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun whenNavigateThroughQuestion_thenUpdatesTitle() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(getExpectedTitle(1, questions.size)))))
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(getExpectedTitle(2, questions.size)))))
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(getExpectedTitle(3, questions.size)))))
        onView(withId(R.id.previousButton)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(getExpectedTitle(2, questions.size)))))
        onView(withId(R.id.previousButton)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(getExpectedTitle(1, questions.size)))))
    }

    @Test
    fun whenNavigateThroughQuestion_thenUpdatesNavigationButtonsState() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.previousButton)).check(matches(not(isEnabled())))
        onView(withId(R.id.nextButton)).check(matches(isEnabled()))

        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.previousButton)).check(matches(isEnabled()))
        onView(withId(R.id.nextButton)).check(matches(isEnabled()))

        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.previousButton)).check(matches(isEnabled()))
        onView(withId(R.id.nextButton)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenEnterAnswer_thenUpdatesSubmitButtonState() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.submitButton)).check(matches(not(isEnabled())))

        onView(withId(R.id.answerInput)).perform(typeText("Answer"))
        onView(withId(R.id.submitButton)).check(matches(isEnabled()))

        onView(withId(R.id.answerInput)).perform(replaceText(""))
        onView(withId(R.id.submitButton)).check(matches(not(isEnabled())))
    }


    @Test
    fun whenSuccessfullySubmitAnswer_thenShowSnackbarWithSuccess() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(intercept = true)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Answer"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.snackbar_text)).check(matches(withText(R.string.survey_answered_successfully)))
        onView(withId(R.id.snackbar_action)).check(matches(not(isCompletelyDisplayed())))
    }

    @Test
    fun whenSuccessfullySubmitAnswer_thenIncrementAnsweredCounter() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(intercept = true)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answeredQuestions)).check(matches(withText(getExpectedAnsweredQuestions(0))))
        onView(withId(R.id.answerInput)).perform(typeText("Answer 1"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.answeredQuestions)).check(matches(withText(getExpectedAnsweredQuestions(1))))
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.answerInput)).perform(typeText("Answer 2"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.answeredQuestions)).check(matches(withText(getExpectedAnsweredQuestions(2))))
    }

    @Test
    fun whenSuccessfullySubmitAnswer_thenDisableInputField() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(intercept = true)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Answer 1"))
        onView(withId(R.id.answerInput)).check(matches(isEnabled()))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.answerInput)).check(matches(not(isEnabled())))

        onView(withId(R.id.nextButton)).perform(click())

        onView(withId(R.id.answerInput)).perform(typeText("Answer 2"))
        onView(withId(R.id.answerInput)).check(matches(isEnabled()))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.answerInput)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenSuccessfullySubmitAnswerAndNavigateThroughQuestions_thenInputFieldAndSubmitButtonDisabledForSubmittedQuestions() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(intercept = true)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Submitted"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.answerInput)).perform(typeText("Not Submitted"))
        onView(withId(R.id.nextButton)).perform(click())

        // third question, answer not entered, not submitted
        onView(withId(R.id.answerInput)).check(matches(isEnabled()))
        onView(withId(R.id.submitButton)).check(matches(not(isEnabled())))
        // go back
        onView(withId(R.id.previousButton)).perform(click())
        // second question, answer entered, not submitted
        onView(withId(R.id.answerInput)).check(matches(isEnabled()))
        onView(withId(R.id.submitButton)).check(matches(isEnabled()))
        // go back
        onView(withId(R.id.previousButton)).perform(click())
        // first question, answer entered, submitted
        onView(withId(R.id.answerInput)).check(matches(not(isEnabled())))
        onView(withId(R.id.submitButton)).check(matches(not(isEnabled())))

    }

    @Test
    fun whenStateSaved_thenStateShouldBeRestored() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(intercept = true)

        val scenario = launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Submitted"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.nextButton)).perform(click())
        onView(withId(R.id.answerInput)).perform(typeText("Not Submitted"))
        onView(withId(R.id.nextButton)).perform(click())

        // Simulate state restoration
        scenario.recreate()

        onView(withId(R.id.toolbar))
            .check(
                matches(
                    hasDescendant(
                        withText(getExpectedTitle(3, questions.size))
                    )
                )
            )
        onView(withId(R.id.answeredQuestions))
            .check(
                matches(
                    withText(getExpectedAnsweredQuestions(1))
                )
            )
        onView(withId(R.id.previousButton)).check(matches(isEnabled()))
        onView(withId(R.id.nextButton)).check(matches(not(isEnabled())))

        // third question, answer not entered, not submitted
        onView(withId(R.id.answerInput)).check(matches(allOf(isEnabled(), withText(""))))
        onView(withId(R.id.submitButton)).check(matches(not(isEnabled())))
        // go back
        onView(withId(R.id.previousButton)).perform(click())
        // second question, answer entered, not submitted
        onView(withId(R.id.answerInput)).check(matches(allOf(isEnabled(), withText("Not Submitted"))))
        onView(withId(R.id.submitButton)).check(matches(isEnabled()))
        // go back
        onView(withId(R.id.previousButton)).perform(click())
        // first question, answer entered, submitted
        onView(withId(R.id.answerInput)).check(matches(allOf(not(isEnabled()), withText("Submitted"))))
        onView(withId(R.id.submitButton)).check(matches(not(isEnabled())))

    }

    @Test
    fun whenSubmitAnswerWithError_thenAnsweredQuestionsNumberNotUpdated() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(error = Throwable())

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answeredQuestions)).check(matches(withText(getExpectedAnsweredQuestions(0))))
        onView(withId(R.id.answerInput)).perform(typeText("Answer"))
        onView(withId(R.id.submitButton)).perform(click())
        onView(withId(R.id.answeredQuestions)).check(matches(withText(getExpectedAnsweredQuestions(0))))
    }

    @Test
    fun whenSubmitAnswerWithGeneralError_thenShowSnackbarWithError() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(error = Throwable())

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Answer"))
        onView(withId(R.id.submitButton)).perform(click())
        checkSnackbarWithError(R.string.survey_error_general)
    }

    @Test
    fun whenSubmitAnswerWithNetworkError_thenShowSnackbarWithError() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)
        DebugSurveyClient.nextSubmitAnswer(error = networkError)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Answer"))
        onView(withId(R.id.submitButton)).perform(click())
        checkSnackbarWithError(R.string.survey_error_network)
    }

    @Test
    fun whenSubmitAnswerWithErrorAndRetryWithoutError_thenShowSnackbarWithSuccess() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Answer"))

        DebugSurveyClient.nextSubmitAnswer(error = Throwable())
        onView(withId(R.id.submitButton)).perform(click())
        checkSnackbarWithError(R.string.survey_error_general)
        // clear error
        DebugSurveyClient.nextSubmitAnswer(intercept = true)
        onView(withId(R.id.snackbar_action)).perform(click())
        onView(withId(R.id.snackbar_text))
            .check(
                matches(
                    withText(R.string.survey_answered_successfully)
                )
            )
        onView(withId(R.id.snackbar_action))
            .check(
                matches(
                    not(isCompletelyDisplayed())
                )
            )
    }

    @Test
    fun whenSubmitAnswerWithErrorAndRetryWithError_thenShowAnotherSnackbarWithError() {
        val questions = listOf(
            Question(1L, "Question 1"),
            Question(2L, "Question 2"),
            Question(3L, "Question 3")
        )
        DebugSurveyClient.nextQuestions(value = questions)

        launchFragmentInContainer<SurveyFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.answerInput)).perform(typeText("Answer"))

        DebugSurveyClient.nextSubmitAnswer(error = Throwable())
        onView(withId(R.id.submitButton)).perform(click())
        checkSnackbarWithError(R.string.survey_error_general)
        // set different error
        DebugSurveyClient.nextSubmitAnswer(error = networkError)
        onView(withId(R.id.snackbar_action)).perform(click())
        checkSnackbarWithError(R.string.survey_error_network)
    }

    private fun getExpectedTitle(currentQuestion: Int, totalQuestions: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.survey_question_number, currentQuestion, totalQuestions)
    }

    private fun getExpectedAnsweredQuestions(answered: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources
            .getQuantityString(R.plurals.survey_answered_questions, answered, answered)
    }

    private fun checkLoadQuestionsWithError(@StringRes expectedMessage: Int) {
        checkLoadQuestionsEmpty()
        // answered questions should not show up when error happened
        onView(withId(R.id.answeredQuestions))
            .check(
                matches(
                    withText("")
                )
            )
        checkSnackbarWithError(expectedMessage)
    }

    private fun checkSnackbarWithError(@StringRes expectedMessage: Int) {
        onView(withId(R.id.snackbar_text))
            .check(
                matches(
                    allOf(
                        //                        isCompletelyDisplayed(),
                        withText(expectedMessage)
                    )
                )
            )
        onView(withId(R.id.snackbar_action))
            .check(
                matches(
                    allOf(
                        //                        isCompletelyDisplayed(),
                        withText(R.string.survey_action_retry)
                    )
                )
            )
    }

    private fun checkLoadQuestionsEmpty() {
        onView(withId(R.id.toolbar))
            .check(
                matches(
                    hasDescendant(
                        withText(R.string.survey_app_name)
                    )
                )
            )
        onView(withId(R.id.emptyView))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        withText(R.string.survey_no_questions)
                    )
                )
            )
        checkVisibleButDisabled(R.id.previousButton, R.id.nextButton, R.id.submitButton, R.id.answerInput)
    }

    private fun checkVisibleButDisabled(@IdRes vararg viewId: Int) {
        viewId.forEach {
            onView(withId(it))
                .check(
                    matches(
                        allOf(
                            isCompletelyDisplayed(),
                            not(isEnabled())
                        )
                    )
                )
        }
    }
}