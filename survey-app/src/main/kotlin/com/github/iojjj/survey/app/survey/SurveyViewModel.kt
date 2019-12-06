package com.github.iojjj.survey.app.survey

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.github.iojjj.survey.networking.Question
import com.github.iojjj.survey.networking.SurveyClient
import com.github.iojjj.survey.core.mvvm.BaseViewModel
import com.github.iojjj.survey.app.survey.SurveyCommand.ReloadQuestions
import com.github.iojjj.survey.app.survey.SurveyCommand.SubmitAnswer
import com.github.iojjj.survey.app.survey.SurveyViewState.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

/**
 * View-Model implementation for Survey screen.
 * @property _surveyClient Networking client that allows to get list of questions and post answers.
 * @property _submittedAnswers All submitted answers.
 * @property _questionsLoaded Flag indicates if questions where loaded for the first time.
 * @property _observeQuestionsTask Questions observation task.
 * @property _questions List of available questions with merged answers.
 * @property _answers Answers for questions.
 * @property _numberOfAnsweredQuestions Number of answered questions.
 * @property _allQuestions All questions as [Observable].
 */
@Suppress("PropertyName")
class SurveyViewModel @Inject internal constructor(@Named("Local") private val _surveyClient: SurveyClient) : BaseViewModel<SurveyViewState>() {

    @VisibleForTesting
    internal var _questionsLoaded = false
    private var _submittedAnswers = mutableMapOf<Long, String>()
    private var _observeQuestionsTask: Disposable? = null
    private var _questions: List<QuestionWithAnswer> = emptyList()
    private val _answers: Subject<Map<Long, String>> = BehaviorSubject.create()

    private val _numberOfAnsweredQuestions: Observable<Int> =
        _answers.map { it.size }
            .startWith(0)
            .distinctUntilChanged()

    private val _allQuestions: Observable<List<QuestionWithAnswer>> =
        Observable.combineLatest(
            _surveyClient.getQuestions().toObservable(),
            _answers.startWith(emptyMap()),
            BiFunction<List<Question>, Map<Long, String>, List<QuestionWithAnswer>> { questions, answers ->
                questions.map { (id, text) ->
                    QuestionWithAnswer(id, text, answers[id] ?: "")
                }
            })

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        observeAnsweredQuestions()
        observeQuestions()
    }

    /**
     * Execute command.
     * @param command any command
     */
    @Suppress("UNUSED_VARIABLE")
    fun execute(command: SurveyCommand) {
        val result: Unit = when (command) {
            ReloadQuestions -> observeQuestions()
            is SubmitAnswer -> submitAnswer(command)
        }
    }

    /**
     * Get question by position.
     * @param position position in list
     * @return requested question or `null` of list is empty or position is out of bounds
     */
    fun getQuestion(position: Int): QuestionWithAnswer? {
        if (_questions.isEmpty() || position < 0 || position >= _questions.size) {
            return null
        }
        return _questions[position]
    }

    private fun observeQuestions() {
        _observeQuestionsTask?.dispose()
        setState(Loading(true))
        _allQuestions
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onQuestionsUpdated, this::onFailedToLoadQuestions)
            .addTo(compositeDisposable)
            .also { _observeQuestionsTask = it }
    }

    private fun onQuestionsUpdated(questions: List<QuestionWithAnswer>) {
        _questions = questions
        setState(Loading(false))
        setState(QuestionsUpdated(questions))
        if (!_questionsLoaded) {
            _questionsLoaded = true
            setState(QuestionsAnswered(0))
        }
    }

    private fun onFailedToLoadQuestions(throwable: Throwable) {
        setState(Loading(false))
        setState(QuestionsUpdated(emptyList()))
        setErrorState(throwable, ReloadQuestions)
    }

    private fun observeAnsweredQuestions() {
        _numberOfAnsweredQuestions
            .skipWhile { !_questionsLoaded }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setState(QuestionsAnswered(it)) }
            .addTo(compositeDisposable)
    }

    private fun submitAnswer(command: SubmitAnswer) {
        setState(Loading(true))
        _surveyClient.submitAnswer(command.questionId, command.answer)
            .doOnComplete {
                _submittedAnswers[command.questionId] = command.answer
                _answers.onNext(_submittedAnswers)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    setState(Loading(false))
                    setState(AnswerSubmitted)
                },
                { throwable ->
                    setState(Loading(false))
                    setErrorState(throwable, command)
                }
            )
            .addTo(compositeDisposable)
    }

    private fun setErrorState(throwable: Throwable, command: SurveyCommand) {
        if (throwable is HttpException) {
            setState(Failure.NetworkError(command))
        } else {
            setState(Failure.GeneralError(command))
        }
    }
}