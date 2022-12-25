package com.treemiddle.orbitsample

import com.treemiddle.orbitsample.model.DeviceModel

data class HomeState(
    val isLoading: Boolean = false,
    val deviceList: List<DeviceModel> = emptyList()
)

sealed class HomeSideEffect {
    data class Dialog(val deviceIndex: Int) : HomeSideEffect()
    object ShowToast : HomeSideEffect()
}
