package com.treemiddle.globalsample

import androidx.lifecycle.viewModelScope
import com.treemiddle.globalsample.contract.HomeContract
import com.treemiddle.globalsample.model.DeviceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    BaseViewModel<HomeContract.HomeEvent, HomeContract.HomeState, HomeContract.HomeEffect>() {

    init {
        initialed()
    }

    override fun createInitialState(): HomeContract.HomeState = HomeContract.HomeState()

    override fun handleEvents(event: HomeContract.HomeEvent) = when (event) {
        is HomeContract.HomeEvent.TitleSelected -> {
            showToast()
        }
        is HomeContract.HomeEvent.DeviceInformationSelected -> {
            showToast()
        }
        is HomeContract.HomeEvent.DeleteDevcieClicked -> {
            showDialog(index = event.devcieIndex)
        }
        is HomeContract.HomeEvent.DialogDeletedClicked -> {
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

    private fun showToast() {
        setEffect { HomeContract.HomeEffect.ShowToast }
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
