package com.example.vitaran

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val token: String, private val username: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("token", token)
            .addHeader("username", username)
            .build()
        return chain.proceed(newRequest)
    }
}