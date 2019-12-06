package com.github.iojjj.survey.app

import android.app.Application
import com.github.iojjj.survey.networking.retrofit.NetworkingModule
import com.github.iojjj.survey.core.CoreModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Main application's component.
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,

        SurveyApplicationModule::class,
        CoreModule::class,
        BuildTypeModule::class,

        NetworkingModule::class,
        SurveyBindings::class
    ]
)
interface SurveyComponent : AndroidInjector<SurveyApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withApplication(application: Application): Builder

        fun build(): SurveyComponent
    }
}