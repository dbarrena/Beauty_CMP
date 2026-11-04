package com.lasso.lassoapp.utils

import com.lasso.lassoapp.model.CommissionCalculationResponse
import lassoapp.composeapp.generated.resources.Res
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi

data class ComisionesPdfFile(val fileName: String, val bytes: ByteArray)

/** Generates a private statement for one employee, using the amounts calculated by the API.
 * [periodEnd] is inclusive. Generation does not mark commissions as paid or send a message.
 */
class ComisionesPdfUtil {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun generate(
        calculation: CommissionCalculationResponse,
        periodStart: LocalDate,
        periodEnd: LocalDate,
    ): ComisionesPdfFile {
        require(periodStart <= periodEnd) { "El periodo de comisiones no es válido." }
        val renderer = ComisionesPdfRenderer(
            Res.readBytes("font/Inter_18pt-Regular.ttf"),
            Res.readBytes("font/Inter_18pt-Bold.ttf"),
            Res.readBytes("files/lasso_pdf_logo.jpg"),
        )
        val statement = CommissionPdfStatement(
            employee = calculation.employee.name,
            period = "${periodStart.label()} al ${periodEnd.label()}",
            serviceSales = calculation.serviceSalesTotal,
            productSales = calculation.productSalesTotal,
            serviceCommission = calculation.serviceCommission,
            productCommission = calculation.productCommission,
            total = calculation.totalCommission,
            services = calculation.services.map {
                CommissionPdfRow(it.name, it.quantity, it.totalSales, it.commissionPercentage, it.commission)
            },
            products = calculation.products.map {
                CommissionPdfRow(it.name, it.quantity, it.totalSales, it.commissionPercentage, it.commission)
            },
        )
        val employeeName = calculation.employee.name
            .map { if (it in "\\/:*?\"<>|" || it.isISOControl()) ' ' else it }
            .joinToString("")
            .replace(Regex("\\s+"), " ")
            .trim(' ', '.')
            .ifEmpty { "Colaborador" }
        return ComisionesPdfFile(
            "Comisiones $employeeName - ${periodStart.fileLabel()} a ${periodEnd.fileLabel()}.pdf",
            renderer.render(statement),
        )
    }
}

private fun LocalDate.fileLabel(): String =
    "${day.toString().padStart(2, '0')}-${(month.ordinal + 1).toString().padStart(2, '0')}-${year.toString().padStart(4, '0')}"

private fun LocalDate.label(): String {
    val months = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
    return "$day ${months[month.ordinal]} $year"
}
