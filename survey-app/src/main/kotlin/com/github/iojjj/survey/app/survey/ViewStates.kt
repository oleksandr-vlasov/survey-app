package com.github.iojjj.survey.app.survey

import androidx.annotation.StringRes
import com.github.iojjj.survey.app.R
import com.github.iojjj.survey.core.mvvm.ViewState

/**
 * Allowed view states for Survey screen.
 */
sealed class SurveyViewState : ViewState {

    /**
     * Something being loaded state.
     * @property isLoading Flag indicates if something being loaded
     */
    data class Loading(val isLoading: Boolean) : SurveyViewState()

    /**
     * Questions loaded state.
     * @property questions List of loaded questions
     */
    data class QuestionsUpdated(val questions: List<QuestionWithAnswer>) : SurveyViewState()
    data class QuestionsAnswered(val questions: Int) : SurveyViewState()
    object AnswerSubmitted : SurveyViewState()
}

sealed class Failure(@StringRes val errorMessage: Int, val command: SurveyCommand) : SurveyViewState() {

    class GeneralError(command: SurveyCommand) : Failure(R.string.survey_error_general, command)
    class NetworkError(command: SurveyCommand) : Failure(R.string.survey_error_network, command)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Failure

        if (errorMessage != other.errorMessage) return false
        if (command != other.command) return false

        return true
    }

    override fun hashCode(): Int {
        var result = errorMessage
        result = 31 * result + command.hashCode()
        return result
    }
}

