package com.example.reto10.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto10.model.Company
import com.example.reto10.network.APIService
import kotlinx.coroutines.launch

class CompanyViewModel: ViewModel() {
    var companyListResponse:List<Company> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")

    fun getCompanyList(){
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                val companyList = apiService.getCompanies()
                companyListResponse = companyList
            }
            catch (e:Exception){
                errorMessage = e.message.toString()
            }
        }
    }
}