package com.lasso.lassoapp.data.remote.endpoints

import com.lasso.lassoapp.data.remote.LASSO_API_URL
import com.lasso.lassoapp.data.remote.bodyOrThrow
import com.lasso.lassoapp.data.remote.recoverWith
import com.lasso.lassoapp.model.MessageResponse
import com.lasso.lassoapp.model.Product
import com.lasso.lassoapp.model.ProductCategory
import com.lasso.lassoapp.model.Service
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface CatalogApi {
    suspend fun getServices(): List<Service>
    suspend fun getProducts(): List<Product>
    suspend fun registerProduct(product: Product): Product
    suspend fun registerService(service: Service): Service
    suspend fun editService(service: Service): Service
    suspend fun editProduct(product: Product): Product
    suspend fun disableService(service: Service): String?
    suspend fun disableProduct(product: Product): String?
    suspend fun getProductCategories(): List<ProductCategory>
    suspend fun registerProductCategory(productCategory: ProductCategory): ProductCategory
}

internal class KtorCatalogApi(private val client: HttpClient) : CatalogApi {
    override suspend fun getServices(): List<Service> = recoverWith(emptyList()) {
        client.get(LASSO_API_URL + "services/all").bodyOrThrow()
    }

    override suspend fun getProducts(): List<Product> = recoverWith(emptyList()) {
        client.get(LASSO_API_URL + "products/all").bodyOrThrow()
    }

    override suspend fun registerProduct(product: Product): Product =
        client.post(LASSO_API_URL + "products/new") {
            contentType(ContentType.Application.Json)
            setBody(product.copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun registerService(service: Service): Service =
        client.post(LASSO_API_URL + "services/new") {
            contentType(ContentType.Application.Json)
            setBody(service.copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun editService(service: Service): Service =
        client.post(LASSO_API_URL + "services/edit") {
            contentType(ContentType.Application.Json)
            setBody(service.copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun editProduct(product: Product): Product =
        client.post(LASSO_API_URL + "products/edit") {
            contentType(ContentType.Application.Json)
            setBody(product.copy(partnerId = null))
        }.bodyOrThrow()

    override suspend fun disableService(service: Service): String? =
        client.post(LASSO_API_URL + "services/disable") {
            contentType(ContentType.Application.Json)
            setBody(service.copy(partnerId = null))
        }.bodyOrThrow<MessageResponse>().message

    override suspend fun disableProduct(product: Product): String? =
        client.post(LASSO_API_URL + "products/disable") {
            contentType(ContentType.Application.Json)
            setBody(product.copy(partnerId = null))
        }.bodyOrThrow<MessageResponse>().message

    override suspend fun getProductCategories(): List<ProductCategory> = recoverWith(emptyList()) {
        client.get(LASSO_API_URL + "product_categories/all").bodyOrThrow()
    }

    override suspend fun registerProductCategory(productCategory: ProductCategory): ProductCategory =
        client.post(LASSO_API_URL + "product_categories/new") {
            contentType(ContentType.Application.Json)
            setBody(productCategory.copy(partnerId = null))
        }.bodyOrThrow()
}
