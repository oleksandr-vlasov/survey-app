package com.github.iojjj.survey.testing

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.ClassRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.TimeUnit

/**
 * Test rule that executes all Rx work immediately. This rule intended to be used with [ClassRule].
 * @property _testScheduler Scheduler that executes work immediately
 */
class RxTestRule : TestRule {

    private val _testScheduler: Scheduler = object : Scheduler() {

        override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
            return super.scheduleDirect(run, 0L, unit)
        }

        override fun createWorker(): Worker {
            return ExecutorScheduler.ExecutorWorker({ it.run() }, false)
        }

    }

    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                RxJavaPlugins.setSingleSchedulerHandler { _testScheduler }
                RxJavaPlugins.setComputationSchedulerHandler { _testScheduler }
                RxJavaPlugins.setIoSchedulerHandler { _testScheduler }
                RxJavaPlugins.setComputationSchedulerHandler { _testScheduler }
                RxJavaPlugins.setNewThreadSchedulerHandler { _testScheduler }
                RxAndroidPlugins.setMainThreadSchedulerHandler { _testScheduler }
                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
    }

}