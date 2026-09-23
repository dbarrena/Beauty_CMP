package com.lasso.lassoapp.data.remote

import com.lasso.lassoapp.data.remote.endpoints.AppointmentsApi
import com.lasso.lassoapp.data.remote.endpoints.AuthApi
import com.lasso.lassoapp.data.remote.endpoints.CashClosureApi
import com.lasso.lassoapp.data.remote.endpoints.CatalogApi
import com.lasso.lassoapp.data.remote.endpoints.ClientsApi
import com.lasso.lassoapp.data.remote.endpoints.EmployeesApi
import com.lasso.lassoapp.data.remote.endpoints.HomeApi
import com.lasso.lassoapp.data.remote.endpoints.KtorAppointmentsApi
import com.lasso.lassoapp.data.remote.endpoints.KtorAuthApi
import com.lasso.lassoapp.data.remote.endpoints.KtorCashClosureApi
import com.lasso.lassoapp.data.remote.endpoints.KtorCatalogApi
import com.lasso.lassoapp.data.remote.endpoints.KtorClientsApi
import com.lasso.lassoapp.data.remote.endpoints.KtorEmployeesApi
import com.lasso.lassoapp.data.remote.endpoints.KtorHomeApi
import com.lasso.lassoapp.data.remote.endpoints.KtorReportsApi
import com.lasso.lassoapp.data.remote.endpoints.KtorSalesApi
import com.lasso.lassoapp.data.remote.endpoints.ReportsApi
import com.lasso.lassoapp.data.remote.endpoints.SalesApi
import io.ktor.client.HttpClient

interface LassoApi :
    AuthApi,
    ClientsApi,
    AppointmentsApi,
    CatalogApi,
    SalesApi,
    EmployeesApi,
    HomeApi,
    CashClosureApi,
    ReportsApi

class KtorLassoApi(client: HttpClient) :
    LassoApi,
    AuthApi by KtorAuthApi(client),
    ClientsApi by KtorClientsApi(client),
    AppointmentsApi by KtorAppointmentsApi(client),
    CatalogApi by KtorCatalogApi(client),
    SalesApi by KtorSalesApi(client),
    EmployeesApi by KtorEmployeesApi(client),
    HomeApi by KtorHomeApi(client),
    CashClosureApi by KtorCashClosureApi(client),
    ReportsApi by KtorReportsApi(client)
