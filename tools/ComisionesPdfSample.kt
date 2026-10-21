package com.lasso.lassoapp.utils

import java.io.File

/** Run tools/generate-comisiones-sample.ps1. Uses the production renderer, not a mock PDF. */
fun main() {
    val resources = File("composeApp/src/commonMain/composeResources")
    fun renderer() = ComisionesPdfRenderer(
        File(resources, "font/Inter_18pt-Regular.ttf").readBytes(),
        File(resources, "font/Inter_18pt-Bold.ttf").readBytes(),
        File(resources, "files/lasso_pdf_logo.jpg").readBytes(),
    )
    val sample = CommissionPdfStatement(
        employee = "María Fernanda López",
        period = "12 oct 2026 al 18 oct 2026",
        serviceSales = 12450.0, productSales = 3650.0,
        serviceCommission = 3735.0, productCommission = 365.0, total = 4100.0,
        services = listOf(
            CommissionPdfRow("Corte y diseño", 8, 3200.0, 30.0, 960.0),
            CommissionPdfRow("Coloración completa", 3, 5400.0, 30.0, 1620.0),
            CommissionPdfRow("Tratamiento de hidratación", 5, 2250.0, 30.0, 675.0),
            CommissionPdfRow("Peinado y acabado", 4, 1600.0, 30.0, 480.0),
        ),
        products = listOf(
            CommissionPdfRow("Shampoo reparación intensiva", 4, 1800.0, 10.0, 180.0),
            CommissionPdfRow("Mascarilla nutritiva", 2, 1200.0, 10.0, 120.0),
            CommissionPdfRow("Aceite protector térmico", 1, 650.0, 10.0, 65.0),
        ),
    )
    File("output/pdf").mkdirs()
    File("tmp/pdfs").mkdirs()
    File("output/pdf/Lasso_Comisiones_Ejemplo.pdf").writeBytes(renderer().render(sample))
    val longRow = "Tratamiento de reparación, hidratación y protección para cabello teñido "
    val stress = sample.copy(
        services = (1..65).map { CommissionPdfRow("Servicio $it - $longRow", it, 1234.56, if (it == 1) null else 12.5, 154.32) } +
            CommissionPdfRow("Detalle extenso " + longRow.repeat(60), 1, 90.0, 10.0, 9.0),
    )
    File("tmp/pdfs/commissions-pagination.pdf").writeBytes(renderer().render(stress))
    File("tmp/pdfs/commissions-empty.pdf").writeBytes(renderer().render(sample.copy(
        serviceSales = 0.0, productSales = 0.0, serviceCommission = 0.0, productCommission = 0.0,
        total = 0.0, services = emptyList(), products = emptyList(),
    )))
    check(runCatching { renderer().render(sample.copy(total = Double.NaN)) }.isFailure)
    println("Generated sample, pagination and empty-state fixtures; rejected non-finite amounts.")
}
