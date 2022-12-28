package com.treemiddle.maverickssample

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.treemiddle.maverickssample.mavericks.hilt.AssistedViewModelFactory
import com.treemiddle.maverickssample.mavericks.hilt.hiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState,
    private val homeUseCase: HomeUseCase
) : MavericksViewModel<HomeState>(initialState) {

    init {
        fetch()
    }

    private fun fetch() = withState { state ->
        suspend { delay(3000); homeUseCase() }
            .execute { result ->
                when (result) {
                    is Success -> {
                        with(result) {
                            state.copy(
                                fetchRequest = this,
                                deviceList = invoke().list,
                                totalDeviceCount = invoke().list.size
                            )
                        }
                    }
                    else -> {
                        state.copy(fetchRequest = result)
                    }
                }
            }
    }

    fun removeDeviceModel(deviceIndex: Int) = setState {
        copy(deviceList = deviceList.filter { it.index != deviceIndex })
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(state: HomeState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, HomeState> by hiltMavericksViewModelFactory()
}
