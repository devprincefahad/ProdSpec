package dev.prince.prodspec.ui.home

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.prodspec.R
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.ui.components.BottomSheet
import dev.prince.prodspec.ui.components.ProductItem
import dev.prince.prodspec.ui.components.SearchBar
import dev.prince.prodspec.ui.theme.Blue
import dev.prince.prodspec.ui.theme.DarkWhite
import dev.prince.prodspec.ui.theme.Gray
import dev.prince.prodspec.ui.theme.poppinsFamily
import dev.prince.prodspec.util.Resource

@SuppressLint("UnrememberedMutableState")
@Composable
@RootNavGraph(start = true)
@Destination
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val products by viewModel.products.collectAsState(initial = Resource.Loading)
    var search by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AddProductFab()
        },
        containerColor = DarkWhite
    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 26.dp)
        ) {

            item {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = "Products Specs",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = poppinsFamily
                    )
                )
            }

            item {
                SearchBar(
                    value = search,
                    onValueChange = {
                        if (it.length <= 20) {
                            search = it
                            viewModel.searchProducts(it)
                        }
                    }
                )
            }

            when (products) {
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Blue,
                                modifier = Modifier
                                    .padding(32.dp)
                                    .size(36.dp)
                            )
                        }
                    }
                }

                is Resource.Success -> {
                    val productList = (products as Resource.Success<List<Product>>).data
                    if (!productList.isNullOrEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                        }
                        items(productList) { item ->
                            ProductItem(product = item)
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No Products available",
                                    color = Blue,
                                    modifier = Modifier
                                        .padding(32.dp),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = poppinsFamily
                                    )
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (products as Resource.Error).message,
                                color = Blue,
                                modifier = Modifier
                                    .padding(32.dp),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = poppinsFamily
                                )
                            )
                        }
                    }
                }
            }
        }

    }
    if (viewModel.showSheet) {
        BottomSheet(
            onDismiss = {
                viewModel.showSheet = false
            },
            content = {
                AddProductForm()
            }
        )
    }
}

@Composable
fun AddProductFab(
    viewModel: HomeViewModel = hiltViewModel()
) {

    ExtendedFloatingActionButton(
        containerColor = Blue,
        text = {
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                text = "Add Product",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontFamily = poppinsFamily
                )
            )
        },
        onClick = {
            viewModel.showSheet = true
        },
        icon = {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.icon_add),
                contentDescription = null,
                tint = Color.White
            )
        }
    )

}


@Composable
fun AddProductForm(viewModel: HomeViewModel = hiltViewModel()) {

    var productName by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var tax by remember { mutableStateOf("") }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            }
        }
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Gray),
                    shape = RoundedCornerShape(8.dp)
                )
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    galleryLauncher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit

                )
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_add_photo),
                        contentDescription = "Add Image",
                        modifier = Modifier
                            .size(80.dp),
                        tint = Gray
                    )
                    Text(
                        text = "Add Image",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray,
                            fontFamily = poppinsFamily
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = productName,
            label = {
                Text(
                    text = "Product Name",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily,
                        color = Gray
                    )
                )
            },
            shape = RoundedCornerShape(8.dp),
            onValueChange = {
                if (it.length <= 25) {
                    productName = it
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontFamily = poppinsFamily,
                color = Color.Black
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = productType,
            label = {
                Text(
                    text = "Product Type",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily,
                        color = Gray
                    )
                )
            },
            shape = RoundedCornerShape(8.dp),
            onValueChange = {
                if (it.length <= 25) {
                    productType = it
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontFamily = poppinsFamily,
                color = Color.Black
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = tax,
            label = {
                Text(
                    text = "Tax",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily,
                        color = Gray
                    )
                )
            },
            shape = RoundedCornerShape(8.dp),
            onValueChange = {
                if (it.length <= 25) {
                    tax = it
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontFamily = poppinsFamily,
                color = Color.Black
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = price,
            label = {
                Text(
                    text = "Price",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily,
                        color = Gray
                    )
                )
            },
            shape = RoundedCornerShape(8.dp),
            onValueChange = {
                if (it.length <= 25) {
                    price = it
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Gray
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontFamily = poppinsFamily,
                color = Color.Black
            )
        )

        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                viewModel.addItem(productName, productType, price, tax, imageUri)
                viewModel.showSheet = false
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Add Product",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}