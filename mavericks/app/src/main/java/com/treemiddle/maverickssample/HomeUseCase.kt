package com.treemiddle.maverickssample

import com.treemiddle.maverickssample.model.DeviceModel
import javax.inject.Inject

class HomeUseCase @Inject constructor() {
    operator fun invoke(): UseCaseModel =
        UseCaseModel(
            list = mutableListOf<DeviceModel>().apply {
                repeat(times = 20) {
                    add(
                        DeviceModel(
                            index = it,
                            name = "Device Name: $it",
                            serialNumber = "Serial Number: $it",
                            isActivated = it == 3
                        )
                    )
                }
            }
        )
}

data class UseCaseModel(val list: List<DeviceModel> = emptyList())
