package com.treemiddle.maverickssample.mavericks.hilt

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope

/**
 * This factory allows Mavericks to supply the initial or restored [MavericksState] to Hilt.
 *
 * Add this interface inside of your [MavericksViewModel] class then create the following Hilt module:
 *
 * @Module
 * @InstallIn(MavericksViewModelComponent::class)
 * interface ViewModelsModule {
 *   @Binds
 *   @IntoMap
 *   @ViewModelKey(MyViewModel::class)
 *   fun myViewModelFactory(factory: MyViewModel.Factory): AssistedViewModelFactory<*, *>
 * }
 *
 * If you already have a ViewModelsModule then all you have to do is add the multibinding entry for your new [MavericksViewModel].
 */
interface AssistedViewModelFactory<VM : MavericksViewModel<S>, S : MavericksState> {
    fun create(state: S): VM
}
