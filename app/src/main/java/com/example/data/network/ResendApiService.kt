package com.example.data.network

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class ResendEmailRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)

@JsonClass(generateAdapter = true)
data class ResendEmailResponse(
    val id: String
)

interface ResendApiService {
    @POST("emails")
    suspend fun sendEmail(
        @Header("Authorization") bearerToken: String,
        @Body request: ResendEmailRequest
    ): ResendEmailResponse
}

object ResendRetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val resendService: ResendApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.resend.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ResendApiService::class.java)
    }
}
