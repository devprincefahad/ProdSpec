package dev.prince.prodspec.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.prodspec.R
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.ui.components.ProductItem
import dev.prince.prodspec.ui.components.SearchBar
import dev.prince.prodspec.ui.theme.Blue
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

    val listState = rememberLazyListState()
    val fabVisibility by derivedStateOf {
        listState.firstVisibleItemIndex == 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AddProductFab(isVisibleBecauseOfScrolling = fabVisibility)
        }
    ) { contentPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 26.dp),
            state = listState
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
}


@Composable
fun AddProductFab(
    isVisibleBecauseOfScrolling: Boolean,
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isVisibleBecauseOfScrolling,
        enter = slideInVertically {
            with(density) { 40.dp.roundToPx() }
        } + fadeIn(),
        exit = fadeOut(
            animationSpec = keyframes {
                this.durationMillis = 120
            }
        )
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
            onClick = { },
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
}