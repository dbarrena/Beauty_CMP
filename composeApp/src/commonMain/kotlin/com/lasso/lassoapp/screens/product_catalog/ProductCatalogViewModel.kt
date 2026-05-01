package com.lasso.lassoapp.screens.product_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lasso.lassoapp.data.remote.LassoApi
import com.lasso.lassoapp.model.LassoItem
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductCatalogViewModel(
    private val lassoApi: LassoApi,
) : ViewModel() {
    private val _state = MutableStateFlow(ProductCatalogState())
    val state: StateFlow<ProductCatalogState> = _state.asStateFlow()

    init {
        refreshAll()
    }

    fun setSelectedTab(tab: ProductCatalogTab) {
        _state.value = _state.value.copy(selectedTab = tab)
    }

    fun refreshAll() {
        refreshProductServices()
        refreshCategories()
    }

    fun refreshProductServices() {
        _state.value = _state.value.copy(
            productsServices = _state.value.productsServices.copy(isLoading = true),
        )
        viewModelScope.launch {
            val products = lassoApi.getProducts()
            val services = lassoApi.getServices()
            val items = products + services
            _state.value = _state.value.copy(
                productsServices = _state.value.productsServices.copy(
                    availableItems = items,
                    filteredItems = applySearch(items, _state.value.productsServices.searchQuery),
                    isLoading = false,
                ),
                categories = _state.value.categories.copy(
                    categoryItemCounts = buildCategoryCounts(
                        categories = _state.value.categories.availableItems,
                        lassoItems = items,
                    ),
                ),
            )
        }
    }

    fun refreshCategories() {
        _state.value = _state.value.copy(
            categories = _state.value.categories.copy(isLoading = true),
        )
        viewModelScope.launch {
            val categories = lassoApi.getProductCategories()
            _state.value = _state.value.copy(
                categories = _state.value.categories.copy(
                    availableItems = categories,
                    categoryItemCounts = buildCategoryCounts(
                        categories = categories,
                        lassoItems = _state.value.productsServices.availableItems,
                    ),
                    isLoading = false,
                ),
            )
        }
    }

    fun onProductServicesSearchChange(query: String) {
        val allItems = _state.value.productsServices.availableItems
        _state.value = _state.value.copy(
            productsServices = _state.value.productsServices.copy(
                searchQuery = query,
                filteredItems = applySearch(allItems, query),
            ),
        )
    }

    fun showProductServiceDialog(item: LassoItem? = null) {
        _state.value = _state.value.copy(
            productsServices = _state.value.productsServices.copy(
                isDialogDisplayed = true,
                selectedItem = item,
            ),
        )
    }

    fun hideProductServiceDialog() {
        _state.value = _state.value.copy(
            productsServices = _state.value.productsServices.copy(
                isDialogDisplayed = false,
                selectedItem = null,
            ),
        )
    }

    fun showDeleteProductServiceConfirmation(item: LassoItem) {
        _state.value = _state.value.copy(
            productsServices = _state.value.productsServices.copy(
                isDeleteConfirmationDisplayed = true,
                selectedItem = item,
            ),
        )
    }

    fun hideDeleteProductServiceConfirmation() {
        _state.value = _state.value.copy(
            productsServices = _state.value.productsServices.copy(
                isDeleteConfirmationDisplayed = false,
                selectedItem = null,
            ),
        )
    }

    fun deleteProductService() {
        val item = _state.value.productsServices.selectedItem ?: return
        viewModelScope.launch {
            // TODO: Call API to delete item
            // lassoApi.deleteProduct(item.id!!) or lassoApi.deleteService(item.id!!)
            hideDeleteProductServiceConfirmation()
            refreshProductServices()
        }
    }

    fun showCategoryDialog(item: ProductCategory? = null) {
        _state.value = _state.value.copy(
            categories = _state.value.categories.copy(
                isDialogDisplayed = true,
                selectedItem = item,
            ),
        )
    }

    fun hideCategoryDialog() {
        _state.value = _state.value.copy(
            categories = _state.value.categories.copy(
                isDialogDisplayed = false,
                selectedItem = null,
            ),
        )
    }

    fun onProductServiceSaved() {
        hideProductServiceDialog()
        refreshProductServices()
    }

    fun onCategorySaved() {
        hideCategoryDialog()
        refreshCategories()
    }

    private fun applySearch(items: List<LassoItem>, query: String): List<LassoItem> {
        if (query.isBlank()) return items
        return items.filter { item ->
            item.name.contains(query, ignoreCase = true)
        }
    }

    private fun buildCategoryCounts(
        categories: List<ProductCategory>,
        lassoItems: List<LassoItem>,
    ): Map<String, Int> {
        val categoryNameCount = lassoItems
            .filterIsInstance<Product>()
            .mapNotNull { it.category }
            .groupingBy { it.trim().lowercase() }
            .eachCount()

        return categories.associate { category ->
            val key = category.name.trim().lowercase()
            key to (categoryNameCount[key] ?: 0)
        }
    }
}
