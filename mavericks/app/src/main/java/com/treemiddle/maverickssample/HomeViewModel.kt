package com.treemiddle.maverickssample

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.treemiddle.maverickssample.mavericks.hilt.AssistedViewModelFactory
import com.treemiddle.maverickssample.mavericks.hilt.hiltMavericksViewModelFactory
import com.treemiddle.maverickssample.model.HomeModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState
) : MavericksViewModel<HomeState>(initialState) {

    private val _homeEffect = Channel<HomeEffect>()
    val homeEffect: Flow<HomeEffect>
        get() = _homeEffect.receiveAsFlow()

    private val _event = MutableSharedFlow<HomeEvent>()

    init {
        initialized()
        subscribeToEvents()
    }

    fun setEvent(event: HomeEvent) = viewModelScope.launch { _event.emit(value = event) }

    private fun initialized() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(timeMillis = 3000)
            setState {
                copy(
                    isLoading = false,
                    deviceList = mutableListOf<HomeModel>().apply {
                        repeat(times = 20) {
                            add(
                                HomeModel(
                                    index = it,
                                    name = "Device Name: $it",
                                    serialNumber = "Serial Number: $it"
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    private fun subscribeToEvents() = viewModelScope.launch {
        _event.collect { handleEvents(event = it) }
    }

    private fun handleEvents(event: HomeEvent) = when (event) {
        is HomeEvent.TitleSelected -> {
            setEffect { HomeEffect.ShowToast }
        }
        is HomeEvent.DeviceInformationSelected -> {
            setEffect { HomeEffect.ShowToast }
        }
        is HomeEvent.DeleteDevcieClicked -> {
            setEffect { HomeEffect.Dialog(deviceIndex = event.devcieIndex) }
        }
        is HomeEvent.DialogDeletedClicked -> {
            removeDeviceModel(deviceIndex = event.deviceIndex)
        }
    }

    private fun setEffect(builder: () -> HomeEffect) {
        viewModelScope.launch { _homeEffect.send(element = builder()) }
    }

    private fun removeDeviceModel(deviceIndex: Int) = setState {
        copy(deviceList = deviceList.filter { it.index != deviceIndex })
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(state: HomeState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, HomeState> by hiltMavericksViewModelFactory()

    sealed class HomeEffect {
        data class Dialog(val deviceIndex: Int) : HomeEffect()
        object ShowToast : HomeEffect()
    }

    sealed class HomeEvent {
        object TitleSelected : HomeEvent()
        object DeviceInformationSelected : HomeEvent()
        data class DeleteDevcieClicked(val devcieIndex: Int) : HomeEvent()
        data class DialogDeletedClicked(val deviceIndex: Int) : HomeEvent()
    }
}
