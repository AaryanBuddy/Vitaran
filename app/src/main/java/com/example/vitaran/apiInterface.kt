package com.example.vitaran

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface apiInterface {
    @POST("Login/LoginLogout")
    fun login(@Body loginRequest: LoginRequest): Call<ResponseData>

    @POST("Guard/AssignRoutes")
    fun report(@Body reportRequest: ReportRequest): Call<ReportResponseData>
}