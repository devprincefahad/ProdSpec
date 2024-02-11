package dev.prince.prodspec.ui.home

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.database.ProductDao
import dev.prince.prodspec.network.ApiService
import dev.prince.prodspec.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService,
    private val productDao: ProductDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Loading)
    val products: StateFlow<Resource<List<Product>>> = _products

    var showSheet by mutableStateOf(false)

    init {
        getProductsList()
    }

    private fun getProductsList() {
        viewModelScope.launch {
            _products.value = Resource.Loading
            try {
                productDao.getProductsFromDB().collect { products ->
                    if (products.isNotEmpty()) {
                        _products.value = Resource.Success(products)
                    } else {
                        val apiProducts = api.getProductsFromApi()
                        productDao.insert(apiProducts)
                        _products.value = Resource.Success(apiProducts)
                    }
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to fetch products")
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            productDao.getProductsFromDB().collect { products ->
                val filteredProducts = products.filter { product ->
                    product.productName.contains(query, ignoreCase = true)
                }
                _products.value = Resource.Success(filteredProducts)
            }
        }
    }

    fun addItem(
        productName: String, productType: String, price: String, tax: String, imageUri: Uri?
    ) {
        val imagePart: List<MultipartBody.Part>? = imageUri?.let { uri ->
            val imageFile: File = getFileFromUri(context, uri)
            val requestBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
            listOf(MultipartBody.Part.createFormData("files[]", imageFile.name, requestBody))
        }
        viewModelScope.launch {
            try {
                api.addItem(
                    productName.toRequestBody(),
                    productType.toRequestBody(),
                    price.toRequestBody(),
                    tax.toRequestBody(),
                    imagePart
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val displayName = "${System.currentTimeMillis()}_${Random.nextInt(0, 1000)}"
        val mimeType = contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "$displayName.$extension")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun addCommasToPrice(price: Float): String {
        val formatter = DecimalFormat("##,##,###.00")
        return formatter.format(price)
    }

}