package com.github.iojjj.survey.core

import androidx.lifecycle.ViewModelProvider
import com.github.iojjj.survey.core.mvvm.DefaultViewModelFactory
import dagger.Binds
import dagger.Module

/**
 * Core application's module that has all required dependencies.
 */
@Module
interface CoreModule {

    @Binds
    fun bindViewModelFactory(factory: DefaultViewModelFactory): ViewModelProvider.Factory
}