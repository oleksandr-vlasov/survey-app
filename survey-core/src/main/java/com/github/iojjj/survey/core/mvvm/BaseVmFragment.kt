package com.github.iojjj.survey.core.mvvm

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import javax.inject.Inject

/**
 * Base fragment implementation that has its own View-Model.
 * @param VM : Type of View-Model
 * @property _vmClass Class of View-Model
 * @property _viewModelFactory View-Model factory
 * @property viewModel Instance of View-Model
 */
abstract class BaseVmFragment<VM : BaseViewModel<*>> protected constructor(
    @LayoutRes layoutId: Int,
    private val _vmClass: Class<out VM>
) :
    BaseFragment(layoutId) {

    private lateinit var _viewModelFactory: ViewModelProvider.Factory
    protected val viewModel: VM by lazy { ViewModelProvider(getStoreOwner(), _viewModelFactory)[_vmClass] }

    @Inject
    internal fun setViewModelFactory(factory: ViewModelProvider.Factory) {
        _viewModelFactory = factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    protected open fun getStoreOwner(): ViewModelStoreOwner = this
}