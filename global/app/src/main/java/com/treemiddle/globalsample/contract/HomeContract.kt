package com.treemiddle.globalsample.contract

import com.treemiddle.globalsample.UiEffect
import com.treemiddle.globalsample.UiEvent
import com.treemiddle.globalsample.UiState
import com.treemiddle.globalsample.model.DeviceModel

class HomeContract {
    sealed class  HomeEvent : UiEvent {
        object TitleClick : HomeEvent()
        data class DeviceInformationClick(val deviceName: String) : HomeEvent()
        data class DeleteDevcieClick(val devcieIndex: Int) : HomeEvent()
        data class DialogDeleteClick(val deviceIndex: Int) : HomeEvent()
    }

    data class HomeState(
        val isLoading: Boolean = false,
        val deviceList: List<DeviceModel> = emptyList(),
        val totalDeviceCount: Int = 0
    ) : UiState

    sealed class HomeEffect : UiEffect {
        data class Dialog(val deviceIndex: Int) : HomeEffect()
        data class ShowToast(val message: String) : HomeEffect()
    }
}
