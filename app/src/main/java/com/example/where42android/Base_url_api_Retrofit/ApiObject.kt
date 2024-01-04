package com.example.where42android.Base_url_api_Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiObject {
    private const val BASE_URL = "http://13.209.149.15:8080/"

    //멤버 한 명 찾기
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: MemberAPI by lazy {
        retrofit.create(MemberAPI::class.java)
    }
}