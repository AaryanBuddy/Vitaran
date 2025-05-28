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

    @POST("Guard/GuardActionHistory")
    fun route(@Body reportRequest: ReportRequest): Call<RouteResponse>

    @POST("Guard/GetDD_revision")
    fun routes(@Body routeRequest: RouteRequest): Call<RoutesResponse>

    @POST("Guard/DD_revisionDeatils")
    fun verification(@Body verificationRequest: VerificationRequest): Call<VerificationResponse>

    @POST("Guard/Deatils_GuardPost")
    fun signature(@Body signatureRequest: SignatureRequest): Call<SignatureResponse>

}