package com.github.iojjj.survey.app.survey

/**
 * Allowed commands for Survey screen.
 */
sealed class SurveyCommand {

    /**
     * Reload questions command.
     */
    object ReloadQuestions : SurveyCommand()

    /**
     * Submit an answer command.
     * @property questionId ID of question
     * @property answer Answer text
     */
    data class SubmitAnswer(val questionId: Long, val answer: String) : SurveyCommand()
}