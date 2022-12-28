package com.treemiddle.maverickssample

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.treemiddle.maverickssample.model.DeviceModel

data class HomeState(
    val fetchRequest: Async<UseCaseModel> = Uninitialized,
    val deviceList: List<DeviceModel> = emptyList(),
    val totalDeviceCount: Int = 0
) : MavericksState
