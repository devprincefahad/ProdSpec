package dev.prince.prodspec.ui.home

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.prince.prodspec.R
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.database.ProductDao
import dev.prince.prodspec.network.ApiService
import dev.prince.prodspec.util.CHANNEL_ID
import dev.prince.prodspec.util.Resource
import kotlinx.coroutines.delay
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

    var category by mutableStateOf("Select Product Category")

    var productType by mutableStateOf("")
    var productName by mutableStateOf("")
    var price by mutableStateOf("")
    var tax by mutableStateOf("")
    var isAddingProduct by mutableStateOf(false)
    var imageUri by mutableStateOf<Uri?>(null)

    var expandedCategoryField by mutableStateOf(false)
    var hideKeyboard by mutableStateOf(false)

    val categories = listOf(
        "Clothing",
        "Electronics",
        "Home & Kitchen",
        "Beauty",
        "Furniture",
        "Grocery",
        "Watches",
        "Other"
    )

    var showProductAddedDialog by mutableStateOf(false)

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
                        _products.value = Resource.Error("Products could not be loaded. Please check your internet connection.")
                    }
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to fetch products from db")
                e.printStackTrace()
            }
        }
        fetchProductListFromNetwork()
    }

    private fun fetchProductListFromNetwork() {
        viewModelScope.launch {
            try {
                val apiProducts = api.getProductsFromApi()
                productDao.insert(apiProducts)
            } catch (e: Exception) {
                e.printStackTrace()
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

    fun addItem() {
        val imagePart: List<MultipartBody.Part>? = imageUri?.let { uri ->
            val imageFile: File = getFileFromUri(context, uri)
            val requestBody: RequestBody =
                RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
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
                fetchProductListFromNetwork()
                showNotification(
                    title = "Product Added",
                    message = "Your $productName has been successfully added."
                )
                delay(2000)
                showProductAddedDialog = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun validateFields(): Boolean {
        if (productName.isBlank() || price.isBlank() || tax.isBlank()
            || productType.isBlank() || productType == "Select Product Category"
        ) {
            Toast.makeText(context, "Please provide all product details", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!tax.isDigitsOnly()) {
            Toast.makeText(context, "Tax should be digits only!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!price.isDigitsOnly()) {
            Toast.makeText(context, "Price should be digits only!", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    fun resetValues() {
        productName = ""
        tax = ""
        price = ""
        imageUri = null
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

    private fun showNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }

}