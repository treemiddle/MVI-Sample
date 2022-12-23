package com.treemiddle.globalsample.contract

import com.treemiddle.globalsample.UiEffect
import com.treemiddle.globalsample.UiEvent
import com.treemiddle.globalsample.UiState
import com.treemiddle.globalsample.model.DeviceModel

class HomeContract {
    sealed class  HomeEvent : UiEvent {
        object TitleSelected : HomeEvent()
        object DeviceInformationSelected : HomeEvent()
        data class DeleteDevcieClicked(val devcieIndex: Int) : HomeEvent()
        data class DialogDeletedClicked(val deviceIndex: Int) : HomeEvent()
    }

    data class HomeState(
        val isLoading: Boolean = false,
        val deviceList: List<DeviceModel> = emptyList()
    ) : UiState

    sealed class HomeEffect : UiEffect {
        data class Dialog(val deviceIndex: Int) : HomeEffect()
        object ShowToast : HomeEffect()
    }
}
