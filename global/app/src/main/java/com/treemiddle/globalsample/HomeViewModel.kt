package com.treemiddle.globalsample

import androidx.lifecycle.viewModelScope
import com.treemiddle.globalsample.contract.HomeContract
import com.treemiddle.globalsample.model.DeviceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(homeUseCase: HomeUseCase) :
    BaseViewModel<HomeContract.HomeEvent, HomeContract.HomeState, HomeContract.HomeEffect>() {

    init {
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            delay(3000)
            when (val result = homeUseCase(params = Unit)) {
                is Result.Success -> {
                    setState {
                        copy(
                            isLoading = false,
                            deviceList = result.data.list
                        )
                    }
                }
                else -> {
                    // error handling
                }
            }
        }
    }

    override fun createInitialState(): HomeContract.HomeState = HomeContract.HomeState()

    override fun handleEvents(event: HomeContract.HomeEvent) = when (event) {
        is HomeContract.HomeEvent.TitleClick -> {
            showToast()
        }
        is HomeContract.HomeEvent.DeviceInformationClick -> {
            showToast(event.deviceName)
        }
        is HomeContract.HomeEvent.DeleteDevcieClick -> {
            showDialog(index = event.devcieIndex)
        }
        is HomeContract.HomeEvent.DialogDeleteClick -> {
            removeDeviceModel(deviceIndex = event.deviceIndex)
        }
    }

    private fun initialed() {
        val random = (0..9).random()
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(timeMillis = 3000)
            setState {
                copy(
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

    private fun showToast(message: String = "") {
        setEffect { HomeContract.HomeEffect.ShowToast(message) }
    }

    private fun showDialog(index: Int) {
        setEffect { HomeContract.HomeEffect.Dialog(deviceIndex = index) }
    }

    private fun removeDeviceModel(deviceIndex: Int) {
        setState {
            copy(
                deviceList = uiState.value.deviceList.filter {
                    it.index != deviceIndex
                }
            )
        }
    }
}
