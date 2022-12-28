package com.treemiddle.globalsample

import com.treemiddle.globalsample.model.DeviceModel
import javax.inject.Inject

class HomeUseCase @Inject constructor() : ResultUseCase<Unit, UseCaseModel>() {
    override suspend fun execute(params: Unit): UseCaseModel =
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
