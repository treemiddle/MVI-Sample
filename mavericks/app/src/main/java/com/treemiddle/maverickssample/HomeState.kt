package com.treemiddle.maverickssample

import com.airbnb.mvrx.MavericksState
import com.treemiddle.maverickssample.model.HomeModel

data class HomeState(
    val isLoading: Boolean = false,
    val deviceList: List<HomeModel> = emptyList()
) : MavericksState
