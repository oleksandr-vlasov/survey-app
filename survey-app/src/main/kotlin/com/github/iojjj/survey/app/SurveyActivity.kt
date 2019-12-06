package com.github.iojjj.survey.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SurveyActivity : AppCompatActivity(R.layout.survey_activity_survey) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
    }
}