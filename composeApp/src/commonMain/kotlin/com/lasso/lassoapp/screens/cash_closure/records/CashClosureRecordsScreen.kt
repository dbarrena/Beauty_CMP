package com.lasso.lassoapp.screens.cash_closure.records

/*@Composable
fun CashClosureRecordsScreen() {
    val viewModel = koinViewModel<CashClosureRecordsScreenViewModel>()
    CashClosureRecordsScreenContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CashClosureRecordsScreenContent(viewModel: CashClosureRecordsScreenViewModel) {
    val state = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    if (state.value.isLoading) {
        FullScreenLoading()
    } else {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetSwipeEnabled = false,
            sheetDragHandle = null,
            sheetTonalElevation = 8.dp,
            sheetShadowElevation = 16.dp,
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 0.dp)
                        .height(500.dp)
                ) {
                    viewModel.state.collectAsState().value.selectedSale?.let {
                        SaleDetailsDialogScreen(it) { shouldReload ->
                            if (shouldReload) viewModel.getCashClosureRecords()

                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    CurrentMonthDateRangePicker { start, end ->
                        if (start != null && end != null) viewModel.getCashClosureRecordsBetweenDates(start, end)
                    }
                }

                Column(
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = "$${state.value.total}",
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        enabled = false,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = MaterialTheme.colorScheme.primary,
                            disabledLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    if (state.value.CashClosureRecords.isEmpty()) {
                        EmptyScreen()
                    }

                    LazyColumn {
                        items(state.value.CashClosureRecords) { sale ->
                            SaleItem(sale) { selectedSale ->
                                println("SaleItem clicked: ${selectedSale.id}")
                                viewModel.setSelectedSale(selectedSale)
                                scope.launch { scaffoldState.bottomSheetState.expand() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SaleItem(sale: SaleApiResponse, onSaleClick: (SaleApiResponse) -> Unit = {}) {
    val paymentType = when (sale.payments.first().paymentType) {
        "cash" -> "Efectivo"
        "transfer" -> "Transferencia"
        else -> "Tarjeta"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onSaleClick(sale) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Total: ${sale.total}", style = MaterialTheme.typography.titleMedium)
            sale.clientId?.let { clientId ->
                Text(text = "Cliente: $clientId", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "Tipo de pago: $paymentType",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha: ${sale.formattedDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            sale.updatedAt?.let { updatedAt ->
                Text(text = "Updated At: $updatedAt", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No hay ventas disponibles",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Text(
                text = "Agrega una venta o modifica el rango de fechas",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}


 */