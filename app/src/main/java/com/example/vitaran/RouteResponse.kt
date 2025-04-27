package com.example.vitaran

data class RouteResponse(
    val `data`: List<RouteDataList>,
    val message: String,
    val status: String
)