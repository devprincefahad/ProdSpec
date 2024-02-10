package dev.prince.prodspec.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.ui.components.ProductItem
import dev.prince.prodspec.ui.components.SearchBar
import dev.prince.prodspec.ui.theme.LightOrange
import dev.prince.prodspec.ui.theme.poppinsFamily
import dev.prince.prodspec.util.Resource

@Composable
@RootNavGraph(start = true)
@Destination
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val products by viewModel.products.collectAsState(initial = Resource.Loading)
    var search by remember { mutableStateOf("") }

    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item(
            span = { GridItemSpan(2) }
        ) {
            SearchBar(
                value = search,
                onValueChange = {
                    if (it.length <= 30) {
                        search = it
                        /*perform search*/
                    }
                }
            )
        }


        when (products) {
            is Resource.Loading -> {
                item(
                    span = { GridItemSpan(2) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LightOrange,
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
                    item(
                        span = { GridItemSpan(2) }
                    ) {
                        Spacer(Modifier.height(16.dp))
                    }
                    items(productList) { item ->
                        ProductItem(
                            navigator = navigator,
                            product = item
                        )
                    }
                } else {
                    item(
                        span = { GridItemSpan(2) }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Products available",
                                color = LightOrange,
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
                item(
                    span = { GridItemSpan(2) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (products as Resource.Error).message,
                            color = LightOrange,
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