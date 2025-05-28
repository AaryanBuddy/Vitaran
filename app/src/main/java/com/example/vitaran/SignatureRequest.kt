package com.example.vitaran

data class SignatureRequest(
    val AdditionalInfo_guard: String,
    val DD_Number: String,
    val ImagesList: List<SignatureImages>,
    val Lat: String,
    val Lon: String,
    val Mat_Doc_No: String,
    val PhotoList: List<SignatureImages>,
    val Remarks: String,
    val Reservation_No: String,
    val Verify: String,
    val VerifybyName: String,
    val routeno: String,
    val username: String
)