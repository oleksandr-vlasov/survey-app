package com.github.iojjj.survey.core.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * Default implementation of View-Model factory.
 * @property _factories All available factories
 */
class DefaultViewModelFactory @Inject internal constructor(
    private val _factories: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = _factories[modelClass] ?: error("ViewModel for \"${modelClass.canonicalName}\" is not registered.")
        return provider.get() as T
    }
}