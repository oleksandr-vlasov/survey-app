package com.github.iojjj.survey.app.survey

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.iojjj.survey.app.R
import com.github.iojjj.survey.app.main.MainFragment
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @Test
    fun whenLaunched_thenUIElementsHasDefaultValuesAndVisible() {
        launchFragmentInContainer<MainFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.icon))
            .check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.title))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        withText(R.string.survey_app_name)
                    )
                )
            )
        onView(withId(R.id.start))
            .check(
                matches(
                    allOf(
                        isCompletelyDisplayed(),
                        withText(R.string.survey_start_survey)
                    )
                )
            )
    }

    @Test
    fun whenStartSurveyClicked_thenNavigatesUserToSurveyScreen() {
        val scenario = launchFragmentInContainer<MainFragment>(themeResId = R.style.AppTheme)

        val controller = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), controller)
        }

        onView(withId(R.id.start)).perform(click())
        verify(controller).navigate(R.id.goToSurvey)
    }
}