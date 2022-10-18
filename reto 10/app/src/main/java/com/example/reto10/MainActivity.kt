package com.example.reto10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto10.data.Departments
import com.example.reto10.model.Company
import com.example.reto10.network.APIService
import com.example.reto10.ui.theme.Reto10Theme
import com.example.reto10.ui.theme.Teal200
import com.example.reto10.view.CompanyItem
import com.example.reto10.view.DropDownDepartments
//import com.example.reto10.view.DropDownDepartments
import com.example.reto10.viewModel.CompanyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val companyViewModel by viewModels<CompanyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Reto10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen(companyViewModel = companyViewModel)
                }
            }
        }
    }
}

@Composable
fun Companylist(companyList: List<Company>) {
    LazyColumn {
        itemsIndexed(items = companyList) { _, item ->
            CompanyItem(company = item)
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, companyViewModel: CompanyViewModel) {
    Column(modifier.padding(vertical = 16.dp)) {
        Text(
            "1000 Empresas mas grandes del país",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Teal200
        )
        DropDownDepartments(Modifier.padding(16.dp))
        Companylist(companyList = companyViewModel.companyListResponse)
        companyViewModel.getCompanyList()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Reto10Theme {
        Column {
            Text(
                "1000 Empresas mas grandes del país",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Teal200
            )
            DropDownDepartments(Modifier.padding(horizontal = 16.dp))
            val company = Company("1", "32423423", "Google", "Software development")
            CompanyItem(company = company)
        }
    }
}