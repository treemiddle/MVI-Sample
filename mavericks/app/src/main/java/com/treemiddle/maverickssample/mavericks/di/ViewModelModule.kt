package com.treemiddle.maverickssample.mavericks.di

import com.treemiddle.maverickssample.HomeViewModel
import com.treemiddle.maverickssample.mavericks.hilt.AssistedViewModelFactory
import com.treemiddle.maverickssample.mavericks.hilt.MavericksViewModelComponent
import com.treemiddle.maverickssample.mavericks.hilt.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun homeViewModelFactory(factory: HomeViewModel.Factory): AssistedViewModelFactory<*, *>
}
