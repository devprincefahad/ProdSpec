package dev.prince.prodspec.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.network.ApiService
import dev.prince.prodspec.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val products: StateFlow<Resource<List<Product>>> = _products

    fun getProductsList() {
        viewModelScope.launch {
            try {
                val response = api.getProducts()
                _products.emit(Resource.Success(response))
                Log.d("api-data", "viewmodel: $response")
            } catch (e: Exception) {
                _products.emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }

}