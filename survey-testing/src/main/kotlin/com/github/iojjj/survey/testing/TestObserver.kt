package com.github.iojjj.survey.testing

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Observer used to test [LiveData].
 * @param T Type of data
 * @property observedValues List of values passed to [LiveData].
 */
class TestObserver<T> : Observer<T> {

    val observedValues = mutableListOf<T?>()

    override fun onChanged(value: T?) {
        observedValues.add(value)
    }
}

/**
 * Start recording values passed to [LiveData].
 * @receiver Any `LiveData`
 * @return newly created observer for testing
 */
fun <T> LiveData<T>.test() = TestObserver<T>().also {
    observeForever(it)
}