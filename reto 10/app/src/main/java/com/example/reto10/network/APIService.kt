package com.example.reto10.network

import com.example.reto10.model.Company
import com.example.reto10.network.ApiConstants.BASE_URL
import com.example.reto10.network.ApiConstants.ENDPOINT
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIService {
    @GET(ENDPOINT)
    suspend fun getCompanies(): List<Company>

    companion object{
        var apiService: APIService? = null
        fun getInstance() : APIService {
            if(apiService == null){
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}