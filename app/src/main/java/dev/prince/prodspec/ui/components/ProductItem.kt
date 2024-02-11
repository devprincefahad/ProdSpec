package dev.prince.prodspec.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.prince.prodspec.R
import dev.prince.prodspec.data.Product
import dev.prince.prodspec.ui.home.HomeViewModel
import dev.prince.prodspec.ui.theme.DarkGray
import dev.prince.prodspec.ui.theme.Blue
import dev.prince.prodspec.ui.theme.poppinsFamily

@Composable
fun ProductItem(
    product: Product,
    viewModel: HomeViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.img_placeholder)
            )

            Text(
                modifier = Modifier
                    .background(Blue)
                    .padding(horizontal = 8.dp),
                text = product.productType,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontFamily = poppinsFamily
                )
            )

        }

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = product.productName,
                    modifier = Modifier
                        .weight(1f),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = poppinsFamily
                    )
                )

                Text(
                    text = "Price: ₹ ${viewModel.addCommasToPrice(product.price.toFloat())}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        fontFamily = poppinsFamily
                    )
                )

            }

            Text(
                text = "Tax: ₹ ${viewModel.addCommasToPrice(product.tax.toFloat())}",
                modifier = Modifier
                    .fillMaxWidth(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray,
                    fontFamily = poppinsFamily
                )
            )
        }


    }
}

