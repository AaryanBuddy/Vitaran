package com.example.vitaran

data class VerificationRequest(
    val DD_Number: String,
    val Mat_Doc_No: String,
    val Reservation_No: String,
    val Route_No: String,
    val username: String
)