package com.example.reto10.network

import com.example.reto10.model.Company
import com.example.reto10.network.ApiConstants.BASE_URL
import com.example.reto10.network.ApiConstants.ENDPOINT
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET(ENDPOINT)
    suspend fun getCompanies(): List<Company>

    @GET(ENDPOINT)
    suspend fun getCompaniesByDepartment(
        @Query(value = "departamento_domicilio") department: String
    ): List<Company>

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