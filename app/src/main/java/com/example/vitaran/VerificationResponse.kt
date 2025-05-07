package com.example.vitaran

data class VerificationResponse(
    val data: List<VerificationDataList>,
    val message: String,
    val status: String
)