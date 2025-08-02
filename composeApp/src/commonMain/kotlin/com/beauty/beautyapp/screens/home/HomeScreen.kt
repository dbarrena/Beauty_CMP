package com.beauty.beautyapp.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beauty.beautyapp.model.Home
import com.beauty.beautyapp.screens.utils.FullScreenLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel = koinViewModel<HomeScreenViewModel>()

    HomeScreenContent(viewModel)
}

@Composable
fun HomeScreenContent(viewModel: HomeScreenViewModel) {
    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(16.dp),
        ) {
            Text(
                text = state.value.partnerName,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W400
                ),
            )

            Text(
                text = state.value.employeeName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.W300
                ),
            )
        }

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 0.dp),
            text = "Mi negocio este mes",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W500
            ),
        )

        if (state.value.isLoading) {
            FullScreenLoading()
        }

        state.value.home?.let {
            MyBusinessGrid(it)
        }
    }
}

@Composable
fun MyBusinessGrid(home: Home) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().height(350.dp),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MyBusinessItem("Numero de ventas", home.numberOfSales.toString())
        }
        item {
            MyBusinessItem("Ganancias", home.totalEarnings)
        }
        item {
            MyBusinessItem("Productos vendidos", home.productsSold.toString())
        }
        item {
            MyBusinessItem("Servicios vendidos", home.servicesSold.toString())
        }
    }
}

@Composable
fun MyBusinessItem(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // square card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f)) // pushes value down
            AutoResizeText(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f)) // pushes title up
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W400,
                ),
                modifier = Modifier
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 26.sp,
    minFontSize: TextUnit = 12.sp,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified
) {
    val textMeasurer = rememberTextMeasurer()
    var fontSize = remember { mutableStateOf(maxFontSize) }

    BoxWithConstraints(modifier = modifier) {
        val availableWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

        LaunchedEffect(text, availableWidthPx, maxFontSize, minFontSize) {
            var currentSize = maxFontSize.value
            while (currentSize >= minFontSize.value) {
                val result = textMeasurer.measure(
                    text = AnnotatedString(text),
                    style = style.copy(
                        fontSize = currentSize.sp,
                        fontWeight = fontWeight,
                        color = color
                    )
                )
                if (result.size.width <= availableWidthPx) {
                    break
                }
                currentSize -= 1
            }
            fontSize.value = currentSize.sp
        }

        Text(
            text = text,
            style = style.copy(
                fontSize = fontSize.value,
                fontWeight = fontWeight,
                color = color
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}
