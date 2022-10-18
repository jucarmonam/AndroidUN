package com.example.reto10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reto10.data.Departments
import com.example.reto10.model.Company
import com.example.reto10.ui.theme.Reto10Theme
import com.example.reto10.ui.theme.Teal200
import com.example.reto10.view.CompanyItem
import com.example.reto10.viewModel.CompanyViewModel
import java.util.*

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
fun Companylist(companyViewModel: CompanyViewModel, state: MutableState<TextFieldValue>) {
    var filteredCompanies: List<Company>
    LazyColumn {
        val searchedText = state.value.text
        filteredCompanies = if (searchedText.isEmpty()) {
            companyViewModel.getCompanyList()
            val companies = companyViewModel.companyListResponse
            companies
        } else {
            companyViewModel.getCompanyListByDepartment(searchedText.uppercase(Locale.getDefault()))
            val resultList = companyViewModel.companyListResponse
            resultList
        }
        itemsIndexed(filteredCompanies) { _, filteredCompany ->
            CompanyItem(company = filteredCompany)
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, companyViewModel: CompanyViewModel) {
    Column(modifier.padding(vertical = 16.dp)) {
        Text(
            "1000 Empresas mas grandes del país",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Teal200
        )
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        DropDownDepartments(Modifier.padding(16.dp), textState)
        companyViewModel.getCompanyList()
        Companylist(companyViewModel, textState)
    }
}

@Composable
fun DropDownDepartments(modifier: Modifier = Modifier, state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        textStyle = TextStyle(fontSize = 18.sp),
        placeholder = {
            Text("Busca un departamento")
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
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
            //DropDownDepartments(Modifier.padding(16.dp), companyViewModel)
            val company = Company("1", "32423423", "Google", "Software development", "BOLIVAR")
            CompanyItem(company = company)
        }
    }
}