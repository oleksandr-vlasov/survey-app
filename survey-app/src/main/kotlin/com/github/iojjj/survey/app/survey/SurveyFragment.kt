package com.github.iojjj.survey.app.survey

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.github.iojjj.survey.app.R
import com.github.iojjj.survey.core.mvvm.BaseVmFragment
import com.github.iojjj.survey.core.text.SimpleTextWatcher
import com.github.iojjj.survey.app.survey.SurveyCommand.SubmitAnswer
import com.github.iojjj.survey.app.survey.SurveyViewState.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.survey_fragment_survey.*
import javax.inject.Inject

/**
 * Implementation of Survey screen.
 * @property _watcher Watcher that tracks entered answers
 * @property _restoringViewPagerState Flag indicates ViewPager's state should be restored
 * @property _currentSnackbar Currently displayed `Snackbar` or `null`
 * @property _lastCurrentItem Last selected question position
 * @property _adapter Adapter that displays list of questions
 */
class SurveyFragment : BaseVmFragment<SurveyViewModel>(R.layout.survey_fragment_survey, SurveyViewModel::class.java) {

    private lateinit var _watcher: AnswerWatcher
    private lateinit var _adapter: QuestionsAdapter
    private var _restoringViewPagerState = false
    private var _currentSnackbar: Snackbar? = null
    private var _lastCurrentItem = 0

    @Inject
    internal fun setAdapter(adapter: QuestionsAdapter) {
        _adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _watcher = savedInstanceState?.getParcelable(KEY_ANSWER_WATCHER) ?: AnswerWatcher()
        _lastCurrentItem = savedInstanceState?.getInt(KEY_CURRENT_ITEM) ?: 0
        _restoringViewPagerState = savedInstanceState != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_ANSWER_WATCHER, _watcher)
        outState.putInt(KEY_CURRENT_ITEM, viewPager.currentItem)
    }

    private fun setupView() {
        setupViewPager()
        setupAnswerInput()
        previousButton.setOnClickListener { viewPager.currentItem -= 1 }
        nextButton.setOnClickListener { viewPager.currentItem += 1 }
        submitButton.setOnClickListener {
            viewModel.getQuestion(viewPager.currentItem)?.run {
                viewModel.execute(SubmitAnswer(id, answerInput.text.toString()))
            }
        }
    }

    private fun setupViewPager() {
        viewPager.isUserInputEnabled = false
        viewPager.adapter = _adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onQuestionChanged(position)
            }
        })
    }

    private fun setupAnswerInput() {
        answerInput.addTextChangedListener(_watcher)
        answerInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitButton.performClick()
                true
            } else {
                false
            }
        }
        answerInput.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                super.afterTextChanged(s)
                submitButton.isEnabled = s.isNotEmpty()
            }
        })
    }

    private fun onQuestionChanged(position: Int) {
        if (_adapter.itemCount <= 0) {
            return
        }
        toolbar.title = getString(R.string.survey_question_number, position + 1, _adapter.itemCount)
        val question = viewModel.getQuestion(position)
        if (question != null) {
            _watcher.onQuestionChanged(question.id)
            answerInput.setText(_watcher.getAnswer(question))
        }
        updateEnabledState(false)
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            @Suppress("UNUSED_VARIABLE") val result: Unit = when (state) {
                is Loading -> onLoadingStateChanged(state)
                is QuestionsUpdated -> onQuestionsLoaded(state)
                is QuestionsAnswered -> onQuestionsAnswered(state)
                AnswerSubmitted -> onAnswerSubmitted()
                is Failure -> onFailure(state)
            }
        })
    }

    private fun onLoadingStateChanged(state: Loading) {
        if (state.isLoading) {
            progress.show()
        } else {
            progress.hide()
        }
        updateEnabledState(state.isLoading)
    }

    private fun updateEnabledState(isLoading: Boolean) {
        if (isLoading) {
            previousButton.isEnabled = false
            nextButton.isEnabled = false
            submitButton.isEnabled = false
            answerInput.isEnabled = false
        } else {
            val position = viewPager.currentItem
            val question = viewModel.getQuestion(position)
            previousButton.isEnabled = position > 0
            nextButton.isEnabled = position < _adapter.itemCount - 1
            answerInput.isEnabled = question?.answer?.isEmpty() ?: false
            submitButton.isEnabled = answerInput.isEnabled && answerInput.text.isNotEmpty()
        }
    }

    private fun onQuestionsLoaded(state: QuestionsUpdated) {
        if (state.questions.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            viewPager.adapter = null
            _adapter.setQuestions(emptyList())
        } else {
            emptyView.visibility = View.GONE
            _adapter.setQuestions(state.questions)
            if (_restoringViewPagerState) {
                _restoringViewPagerState = false
                viewPager.setCurrentItem(_lastCurrentItem, false)
            }
        }
    }

    private fun onQuestionsAnswered(state: QuestionsAnswered) {
        answeredQuestions.text = resources.getQuantityString(R.plurals.survey_answered_questions, state.questions, state.questions)
    }

    private fun onAnswerSubmitted() {
        showSnackbar { makeSnackbar(R.string.survey_answered_successfully, Snackbar.LENGTH_SHORT) }
    }

    private fun onFailure(state: Failure) {
        showSnackbar {
            makeSnackbar(state.errorMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.survey_action_retry) { viewModel.execute(state.command) }
        }
    }

    private fun showSnackbar(snackbarProvider: () -> Snackbar) {
        val snackbar = _currentSnackbar
        if (snackbar != null) {
            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    snackbarProvider().show()
                }
            })
        } else {
            snackbarProvider().show()
        }
    }

    private fun makeSnackbar(@StringRes message: Int, @BaseTransientBottomBar.Duration duration: Int): Snackbar {
        return Snackbar.make(snackbarAnchor, message, duration)
            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar, event: Int) {
                    super.onDismissed(snackbar, event)
                    if (_currentSnackbar == snackbar) {
                        _currentSnackbar = null
                    }
                }
            })
    }

    companion object {

        private const val KEY_ANSWER_WATCHER = "answer_watcher"
        private const val KEY_CURRENT_ITEM = "current_item"
    }
}