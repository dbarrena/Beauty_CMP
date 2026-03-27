package com.lasso.lassoapp.screens.pos

enum class PosCatalogFilter {
    ALL,
    SERVICES,
    PRODUCTS,
}

fun PosCatalogFilter.label(): String = when (this) {
    PosCatalogFilter.ALL -> "Todas"
    PosCatalogFilter.SERVICES -> "Servicios"
    PosCatalogFilter.PRODUCTS -> "Productos"
}
