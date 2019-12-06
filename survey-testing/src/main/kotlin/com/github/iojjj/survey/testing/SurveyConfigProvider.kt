package com.github.iojjj.survey.testing

import android.os.Build
import com.google.auto.service.AutoService
import org.robolectric.annotation.Config
import org.robolectric.pluginapi.config.GlobalConfigProvider

/**
 * Robolectric configuration provider.
 *
 * When targeting [Build.VERSION_CODES.P] and greater, Robolectric requires Java 9 to be installed, that I don't have at this moment.
 */
@AutoService(GlobalConfigProvider::class)
class SurveyConfigProvider : GlobalConfigProvider {

    override fun get(): Config {
        return Config.Builder()
            .setSdk(Build.VERSION_CODES.O_MR1)
            .build()
    }

}