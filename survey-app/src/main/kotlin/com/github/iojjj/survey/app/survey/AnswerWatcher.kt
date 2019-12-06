package com.github.iojjj.survey.app.survey

import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import com.github.iojjj.survey.core.text.SimpleTextWatcher

/**
 * Implementation of [TextWatcher] that persists entered but not submitted answers.
 * @property _questionId ID of currently selected question
 * @property _enteredAnswers Map with entered answers.
 */
@Suppress("UNCHECKED_CAST")
class AnswerWatcher() : SimpleTextWatcher(), Parcelable {

    private var _questionId = 0L
    private val _enteredAnswers: MutableMap<Long, String> = mutableMapOf()

    constructor(parcel: Parcel) : this() {
        _questionId = parcel.readLong()
        parcel.readMap(_enteredAnswers as Map<Any?, Any?>, javaClass.classLoader)
    }

    override fun afterTextChanged(s: Editable) {
        super.afterTextChanged(s)
        require(_questionId > 0) { "Question ID is not set yet." }
        _enteredAnswers[_questionId] = s.toString()
    }

    /**
     * Should be called when selected question changes.
     * @param questionId ID of question
     */
    fun onQuestionChanged(questionId: Long) {
        _questionId = questionId
    }

    /**
     * Get answer text that should be displayed to user.
     * @param question Any question.
     * @return answer text
     */
    fun getAnswer(question: QuestionWithAnswer): String {
        return if (question.answer.isNotEmpty()) {
            question.answer
        } else {
            _enteredAnswers[question.id] ?: question.answer
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_questionId)
        parcel.writeMap(_enteredAnswers as Map<Any?, Any?>)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnswerWatcher

        if (_questionId != other._questionId) return false
        if (_enteredAnswers != other._enteredAnswers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _questionId.hashCode()
        result = 31 * result + _enteredAnswers.hashCode()
        return result
    }


    companion object CREATOR : Parcelable.Creator<AnswerWatcher> {
        override fun createFromParcel(parcel: Parcel): AnswerWatcher {
            return AnswerWatcher(parcel)
        }

        override fun newArray(size: Int): Array<AnswerWatcher?> {
            return arrayOfNulls(size)
        }
    }


}