package com.github.iojjj.survey.core.mvvm

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Base implementation of [ViewModel] that allows to update view's state.
 * @param S: Type of state
 * @property compositeDisposable [CompositeDisposable] that will be disposed in [onCleared]
 * @property state [LiveData] that emits state
 */
abstract class BaseViewModel<S : ViewState> : ViewModel(), LifecycleObserver {

    private val _state = MutableLiveData<S>()
    protected val compositeDisposable = CompositeDisposable()

    val state: LiveData<S>
        get() = _state

    protected fun setState(state: S) {
        _state.value = state
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    @VisibleForTesting
    fun cleanUp() {
        onCleared()
    }
}