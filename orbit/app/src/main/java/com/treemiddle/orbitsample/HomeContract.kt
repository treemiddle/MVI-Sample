package com.treemiddle.orbitsample

import com.treemiddle.orbitsample.model.DeviceModel

data class HomeState(
    val isLoading: Boolean = false,
    val deviceList: List<DeviceModel> = emptyList(),
    val totalDeviceCount: Int = 0
)

sealed class HomeSideEffect {
    data class Dialog(val deviceIndex: Int) : HomeSideEffect()
    data class ShowToast(val message: String) : HomeSideEffect()
}
