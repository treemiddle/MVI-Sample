package com.treemiddle.orbitsample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> = container(HomeState())

    private val _event = MutableSharedFlow<HomeEvent>()

    init {
        subscribeToEvents()

        intent {
            reduce { state.copy(isLoading = true) }

            delay(3000)
            when (val result = homeUseCase(params = Unit)) {
                is Result.Success -> {
                    reduce {
                        state.copy(
                            isLoading = false,
                            deviceList = result.data.list,
                            totalDeviceCount = result.data.list.size
                        )
                    }
                }
                else -> {
                    // error handling
                }
            }
        }
    }

    fun setEvent(event: HomeEvent) = viewModelScope.launch { _event.emit(value = event) }

    private fun subscribeToEvents() = viewModelScope.launch {
        _event.collect { handleEvents(event = it) }
    }

    private fun handleEvents(event: HomeEvent) = when (event) {
        is HomeEvent.TitleClick -> {
            setEffect(HomeSideEffect.ShowToast(""))
        }
        is HomeEvent.DeviceInformationClick -> {
            setEffect(HomeSideEffect.ShowToast(event.deviceName))
        }
        is HomeEvent.DeleteDeviceClick -> {
            setEffect(HomeSideEffect.Dialog(deviceIndex = event.devcieIndex))
        }
        is HomeEvent.DialogDelteClick -> {
            removeDeviceModel(deviceIndex = event.deviceIndex)
        }
    }

    private fun setEffect(homeSideEffect: HomeSideEffect) = intent {
        postSideEffect(homeSideEffect)
    }

    private fun removeDeviceModel(deviceIndex: Int) = intent {
        reduce {
            state.copy(deviceList = state.deviceList.filter { it.index != deviceIndex })
        }
    }

    sealed class HomeEvent {
        object TitleClick : HomeEvent()
        data class DeviceInformationClick(val deviceName: String) : HomeEvent()
        data class DeleteDeviceClick(val devcieIndex: Int) : HomeEvent()
        data class DialogDelteClick(val deviceIndex: Int) : HomeEvent()
    }
}
