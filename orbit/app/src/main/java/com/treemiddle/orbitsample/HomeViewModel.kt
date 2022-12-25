package com.treemiddle.orbitsample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treemiddle.orbitsample.model.DeviceModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class HomeViewModel : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container: Container<HomeState, HomeSideEffect> = container(HomeState())

    private val _event = MutableSharedFlow<HomeEvent>()

    init {
        initialed()
        subscribeToEvents()
    }

    fun setEvent(event: HomeEvent) = viewModelScope.launch { _event.emit(value = event) }

    private fun initialed() {
        val random = (0..9).random()

        intent {
            reduce { state.copy(isLoading = true) }
            delay(timeMillis = 3000)
            reduce {
                state.copy(
                    isLoading = false,
                    deviceList = mutableListOf<DeviceModel>().apply {
                        repeat(times = 20) {
                            add(
                                DeviceModel(
                                    index = it,
                                    name = "Device Name: $it",
                                    serialNumber = "Serial Number: $it",
                                    isActivated = it == random
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
            setEffect(HomeSideEffect.ShowToast)
        }
        is HomeEvent.DeviceInformationSelected -> {
            setEffect(HomeSideEffect.ShowToast)
        }
        is HomeEvent.DeleteDevcieClicked -> {
            setEffect(HomeSideEffect.Dialog(deviceIndex = event.devcieIndex))
        }
        is HomeEvent.DialogDeletedClicked -> {
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
        object TitleSelected : HomeEvent()
        object DeviceInformationSelected : HomeEvent()
        data class DeleteDevcieClicked(val devcieIndex: Int) : HomeEvent()
        data class DialogDeletedClicked(val deviceIndex: Int) : HomeEvent()
    }
}
