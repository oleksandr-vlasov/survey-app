package com.github.iojjj.survey.app.survey

/**
 * Entity that holds information about question and answer.
 * @property id ID of question.
 * @property question Question text.
 * @property answer Answer text.
 */
data class QuestionWithAnswer(val id: Long, val question: String, val answer: String)