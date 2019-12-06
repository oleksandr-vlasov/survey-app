package com.github.iojjj.survey.testing

import io.reactivex.Single

/**
 * Helper class that allows to postpone actual stream creation and specify return values at runtime.
 * @param T Type of returned values
 * @property _value Value that will be actually returned.
 * @property _error Error that will be actually thrown.
 */
class RxDeferred<T : Any> {

    private var _value: T? = null
    private var _error: Throwable? = null

    /**
     * Set next value or error.
     * @param value next value to emit
     * @param error next error to throw
     */
    fun next(value: T? = null, error: Throwable? = null) {
        _value = value
        _error = error
    }

    fun single(): Single<T> {
        return Single.defer {
            val value = _value
            val error = _error
            when {
                error != null -> Single.error(error)
                value != null -> Single.just(value)
                else -> throw error("Nor value neither error has been set.")
            }
        }
    }
}