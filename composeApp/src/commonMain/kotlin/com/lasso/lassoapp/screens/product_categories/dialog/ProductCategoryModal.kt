package com.lasso.lassoapp.screens.product_categories.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.screens.product_service.dialog.ProductServiceDialogViewModel
import com.lasso.lassoapp.screens.utils.FullScreenLoading
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductCategoryModal(
    productCategory: ProductCategory?,
    onDismiss: () -> Unit,
    onProductCategoryRegistered: (productCategory: ProductCategory) -> Unit,
) {
    val viewModel = koinViewModel<ProductCategoryModalViewModel>()

    ProductCategoryModalContent(viewModel, productCategory, onDismiss, onProductCategoryRegistered)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductCategoryModalContent(
    viewModel: ProductCategoryModalViewModel,
    productCategory: ProductCategory?,
    onDismiss: () -> Unit,
    onProductCategoryRegistered: (productCategory: ProductCategory) -> Unit
) {
    val state = viewModel.state.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val nameState = remember { mutableStateOf(productCategory?.name ?: "") }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Categoria Producto",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            )

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            ProductDialogButton(state.value.isLoading) {
                val productCategory = ProductCategory(
                    name = nameState.value
                )
                viewModel.registerProductCategory(productCategory)
            }
        }
    }

    LaunchedEffect(state.value.registeredProductCategory) {
        state.value.registeredProductCategory?.let {
            viewModel.resetState()
            onProductCategoryRegistered(it)
        }
    }
}

@Composable
private fun ProductDialogButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        if (isLoading) {
            FullScreenLoading(Color.White)
        } else{
            Text(
                "Registrar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
        }
    }
}