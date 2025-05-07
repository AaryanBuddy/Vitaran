package com.example.vitaran

data class RoutesResponse(
    val `data`: List<RoutesDataList>,
    val message: String,
    val status: String
)