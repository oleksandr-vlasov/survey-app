package com.github.iojjj.survey.app.survey

import android.os.Parcel
import android.text.SpannableStringBuilder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AnswerWatcherTest {

    private lateinit var classToTest: AnswerWatcher

    @Before
    fun setUp() {
        classToTest = AnswerWatcher()
    }

    @Test(expected = IllegalArgumentException::class)
    fun afterTextChanged_whenQuestionNotSelected_thenThrowsException() {
        val text = SpannableStringBuilder("Text")
        classToTest.afterTextChanged(text)
    }

    @Test
    fun afterTextChanged_whenQuestionSelected_thenUpdatesAnswer() {
        val question = QuestionWithAnswer(1L, "Question", "")
        classToTest.onQuestionChanged(1L)
        val text1 = SpannableStringBuilder("Text 1")
        classToTest.afterTextChanged(text1)

        assertEquals("Text 1", classToTest.getAnswer(question))

        val text2 = SpannableStringBuilder("Text 2")
        classToTest.afterTextChanged(text2)

        assertEquals("Text 2", classToTest.getAnswer(question))
    }

    @Test
    fun onQuestionChanged_whenInvoked_thenSavesAnswersForSelectedQuestion() {
        classToTest.onQuestionChanged(1L)
        val text1 = SpannableStringBuilder("Text 1")
        classToTest.afterTextChanged(text1)

        val question1 = QuestionWithAnswer(1L, "Question 1", "")
        val question2 = QuestionWithAnswer(2L, "Question 2", "")
        assertEquals("Text 1", classToTest.getAnswer(question1))
        assertEquals("", classToTest.getAnswer(question2))
    }

    @Test
    fun getAnswer_whenNoAnswerForQuestion_thenReturnsEmptyString() {
        val question = QuestionWithAnswer(1L, "Question", "")
        assertEquals("", classToTest.getAnswer(question))
    }

    @Test
    fun getAnswer_whenHasAnswerForQuestion_thenReturnsAnswer() {
        classToTest.onQuestionChanged(1L)
        classToTest.afterTextChanged(SpannableStringBuilder("Text"))

        val question = QuestionWithAnswer(1L, "Question", "")
        assertEquals("Text", classToTest.getAnswer(question))
    }

    @Test
    fun getAnswer_whenHasAnswerForQuestionButQuestionAlreadyAnswered_thenReturnsAlreadyAnsweredText() {
        classToTest.onQuestionChanged(1L)
        classToTest.afterTextChanged(SpannableStringBuilder("Text"))

        val question = QuestionWithAnswer(1L, "Question", "Different Text")
        assertEquals("Different Text", classToTest.getAnswer(question))
    }

    @Test
    fun writeToParcel_whenStoringToParcel_thenCanBeProperlyRestoredFromParcel() {
        val parcel = Parcel.obtain()
        parcel.setDataPosition(0)
        classToTest.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)
        val anotherInstance = AnswerWatcher(parcel)
        assertEquals(classToTest, anotherInstance)
    }
}