package com.treemiddle.maverickssample.model

data class HomeModel(
    val index: Int = 0,
    val name: String,
    val serialNumber: String,
    val isActivated: Boolean = false
)
