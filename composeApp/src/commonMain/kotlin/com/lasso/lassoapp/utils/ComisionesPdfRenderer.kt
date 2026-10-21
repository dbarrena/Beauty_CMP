package com.lasso.lassoapp.utils

import kotlin.math.roundToLong

internal data class CommissionPdfRow(val name: String, val quantity: Int, val sales: Double, val percentage: Double?, val commission: Double)
internal data class CommissionPdfStatement(
    val employee: String, val period: String,
    val serviceSales: Double, val productSales: Double,
    val serviceCommission: Double, val productCommission: Double, val total: Double,
    val services: List<CommissionPdfRow>, val products: List<CommissionPdfRow>,
)

/** Shared vector layout: the sample and both mobile platforms use this exact renderer. */
internal class ComisionesPdfRenderer(regular: ByteArray, bold: ByteArray, private val logo: ByteArray) {
    private val fonts = listOf(PdfTrueTypeFont(regular, "InterRegular"), PdfTrueTypeFont(bold, "InterBold"))
    private val pages = mutableListOf<StringBuilder>()
    private val ink = "0.208 0.239 0.235"
    private val muted = "0.478 0.510 0.502"
    private var y = 0.0
    private lateinit var statement: CommissionPdfStatement
    private val page get() = pages.last()

    fun render(data: CommissionPdfStatement): ByteArray {
        require(listOf(data.serviceSales, data.productSales, data.serviceCommission, data.productCommission, data.total)
            .all { it.isFinite() }) { "Los importes de comisiones no son válidos." }
        require((data.services + data.products).all {
            it.sales.isFinite() && it.commission.isFinite() && (it.percentage?.isFinite() != false)
        }) { "El detalle de comisiones no es válido." }
        statement = data
        pages.clear()
        newPage()
        text("Comisiones", 36.0, 105.0, 28.0, bold = true)
        text("El detalle de tu trabajo, en un solo lugar.", 36.0, 126.0, 10.0, color = muted)
        y = 148.0
        text("COLABORADOR", 36.0, y, 8.0, color = muted)
        val employeeLines = wrap(data.employee, 523.0, 16.0, true)
        employeeLines.forEachIndexed { i, line -> text(line, 36.0, y + 24 + i * 20, 16.0, true) }
        y += 44 + (employeeLines.size - 1) * 20
        text("PERIODO  ${data.period}", 36.0, y, 10.0, color = muted)
        y += 22
        ensure(143.0)
        rect(36.0, y, 523.0, 123.0, "0.918 0.980 0.965", 14.0)
        text("TOTAL DE COMISIONES", 54.0, y + 25, 9.0, color = ink)
        val totalText = money(data.total)
        val totalSize = if (width(totalText, 34.0, true) < 410) 34.0 else 25.0
        text(totalText, 54.0, y + 66, totalSize, true)
        text("MXN", 541.0, y + 62, 9.0, true, right = true)
        line(54.0, y + 81, 541.0, y + 81)
        text("Servicios", 54.0, y + 105, 10.0, color = muted)
        text(money(data.serviceCommission), 280.0, y + 105, 11.0, true, right = true)
        text("Productos", 314.0, y + 105, 10.0, color = muted)
        text(money(data.productCommission), 541.0, y + 105, 11.0, true, right = true)
        y += 147
        section("Servicios", data.services, data.serviceSales, data.serviceCommission, "0.0 0.820 0.678")
        y += 12
        section("Productos", data.products, data.productSales, data.productCommission, "1.0 0.478 0.373")
        pages.forEachIndexed { i, p ->
            lineOn(p, 36.0, 789.0, 559.0, 789.0)
            textOn(p, "Generado con Lasso", 36.0, 808.0, 8.0, color = muted)
            textOn(p, "${i + 1} / ${pages.size}", 559.0, 808.0, 8.0, right = true, color = muted)
            textOn(p, "Importes en MXN. Este reporte no acredita un pago.", 36.0, 824.0, 7.0, color = muted)
        }
        return buildPdf(pages.map { it.toString() }, fonts, logo)
    }

    private fun newPage() {
        pages += StringBuilder()
        // The brand gradient is kept to a thin accent; dark text provides readable contrast.
        for (i in 0 until 120) {
            val t = i / 119.0
            val a = if (t < .5) doubleArrayOf(0.0, 209.0, 173.0) else doubleArrayOf(255.0, 207.0, 60.0)
            val b = if (t < .5) doubleArrayOf(255.0, 207.0, 60.0) else doubleArrayOf(255.0, 122.0, 95.0)
            val f = if (t < .5) t * 2 else (t - .5) * 2
            rect(i * 595.0 / 120, 0.0, 595.0 / 120 + .1, 5.0, a.indices.joinToString(" ") { n((a[it] + (b[it] - a[it]) * f) / 255) })
        }
        page.append("q 112 0 0 30.8 36 771.2 cm /Logo Do Q\n")
        text("REPORTE DE COMISIONES", 559.0, 56.0, 8.0, true, right = true, color = muted)
        if (pages.size > 1) {
            text("Comisiones · continuación", 36.0, 105.0, 17.0, true)
            val header = "${statement.employee} | ${statement.period}"
            val lines = wrap(header, 523.0, 9.0, false)
            lines.forEachIndexed { i, s -> text(s, 36.0, 126.0 + i * 13, 9.0, color = muted) }
            y = 145.0 + (lines.size - 1) * 13
        } else y = 100.0
    }

    private fun ensure(height: Double) { if (y + height > 764) newPage() }

    private fun section(title: String, rows: List<CommissionPdfRow>, sales: Double, commission: Double, accent: String) {
        ensure(109.0)
        sectionHeader(title, accent)
        if (rows.isEmpty()) {
            text("Sin ${title.lowercase()} en este periodo.", 48.0, y + 24, 10.0, color = muted)
            y += 42
        }
        rows.forEachIndexed { rowIndex, row ->
            // Split even a single unusually long name across pages without dropping content.
            val lines = wrap(row.name, 214.0, 10.0, false)
            val fullHeight = maxOf(32.0, lines.size * 14.0 + 16)
            if (fullHeight <= 550 && y + fullHeight > 764) {
                newPage()
                sectionHeader("$title · continuación", accent)
            }
            var index = 0
            while (index < lines.size) {
                if (y + 35 > 764) { newPage(); sectionHeader("$title · continuación", accent) }
                val count = minOf(lines.size - index, ((764 - y - 20) / 14).toInt().coerceAtLeast(1))
                val height = maxOf(32.0, count * 14.0 + 16)
                rect(36.0, y, 523.0, height, if (rowIndex % 2 == 0) "0.973 0.973 0.980" else "1 1 1")
                for (i in 0 until count) text(lines[index + i], 48.0, y + 22 + i * 14, 10.0)
                if (index == 0) {
                    text(row.quantity.toString(), 302.0, y + 22, 9.0, right = true)
                    text(money(row.sales), 397.0, y + 22, 9.0, right = true)
                    text(row.percentage?.let { "${number(it)}%" } ?: "Variable", 461.0, y + 22, 9.0, right = true)
                    text(money(row.commission), 548.0, y + 22, 9.0, true, right = true)
                }
                y += height
                index += count
            }
        }
        ensure(32.0)
        line(36.0, y, 559.0, y)
        text("Subtotal ${title.lowercase()}", 48.0, y + 21, 10.0, true)
        text(money(sales), 397.0, y + 21, 9.0, right = true)
        text(money(commission), 548.0, y + 21, 10.0, true, right = true)
        y += 28
    }

    private fun sectionHeader(title: String, accent: String) {
        rect(36.0, y + 1, 4.0, 18.0, accent, 2.0)
        text(title, 48.0, y + 15, 13.0, true)
        y += 31
        text("CONCEPTO", 48.0, y + 11, 7.0, true, color = muted)
        text("CANT.", 302.0, y + 11, 7.0, true, right = true, color = muted)
        text("VENTAS", 397.0, y + 11, 7.0, true, right = true, color = muted)
        text("TASA", 461.0, y + 11, 7.0, true, right = true, color = muted)
        text("COMISIÓN", 548.0, y + 11, 7.0, true, right = true, color = muted)
        y += 21
    }

    private fun width(s: String, size: Double, bold: Boolean) = fonts[if (bold) 1 else 0].width(s) * size / 1000
    private fun wrap(s: String, max: Double, size: Double, bold: Boolean): List<String> {
        val result = mutableListOf<String>()
        var current = ""
        s.replace(Regex("\\s+"), " ").trim().forEach { char ->
            if (width(current + char, size, bold) > max && current.isNotEmpty()) {
                val split = current.lastIndexOf(' ')
                if (split > 0) { result += current.substring(0, split); current = current.substring(split + 1) }
                else { result += current; current = "" }
            }
            current += char
        }
        result += current.trim()
        return result
    }

    private fun text(s: String, x: Double, top: Double, size: Double, bold: Boolean = false, right: Boolean = false, color: String = ink) =
        textOn(page, s, x, top, size, bold, right, color)
    private fun textOn(p: StringBuilder, s: String, x: Double, top: Double, size: Double, bold: Boolean = false, right: Boolean = false, color: String = ink) {
        val font = fonts[if (bold) 1 else 0]
        val left = if (right) x - width(s, size, bold) else x
        p.append("BT $color rg /F${if (bold) 2 else 1} ${n(size)} Tf 1 0 0 1 ${n(left)} ${n(842 - top)} Tm <${font.encode(s)}> Tj ET\n")
    }
    private fun line(x: Double, top: Double, end: Double, bottom: Double) = lineOn(page, x, top, end, bottom)
    private fun lineOn(p: StringBuilder, x: Double, top: Double, end: Double, bottom: Double) {
        p.append("0.898 0.906 0.922 RG 0.6 w ${n(x)} ${n(842 - top)} m ${n(end)} ${n(842 - bottom)} l S\n")
    }
    private fun rect(x: Double, top: Double, w: Double, h: Double, color: String, radius: Double = 0.0) {
        val b = 842 - top - h
        if (radius == 0.0) page.append("$color rg ${n(x)} ${n(b)} ${n(w)} ${n(h)} re f\n")
        else {
            val r = radius; val c = r * .552285
            page.append("$color rg ${n(x+r)} ${n(b)} m ${n(x+w-r)} ${n(b)} l ${n(x+w-r+c)} ${n(b)} ${n(x+w)} ${n(b+r-c)} ${n(x+w)} ${n(b+r)} c ${n(x+w)} ${n(b+h-r)} l ${n(x+w)} ${n(b+h-r+c)} ${n(x+w-r+c)} ${n(b+h)} ${n(x+w-r)} ${n(b+h)} c ${n(x+r)} ${n(b+h)} l ${n(x+r-c)} ${n(b+h)} ${n(x)} ${n(b+h-r+c)} ${n(x)} ${n(b+h-r)} c ${n(x)} ${n(b+r)} l ${n(x)} ${n(b+r-c)} ${n(x+r-c)} ${n(b)} ${n(x+r)} ${n(b)} c f\n")
        }
    }
}

private fun n(value: Double): String = ((value * 1000).roundToLong() / 1000.0).toString()
private fun number(value: Double): String = if (value == value.toLong().toDouble()) value.toLong().toString() else n(value)
private fun money(value: Double): String {
    val cents = (kotlin.math.abs(value) * 100).roundToLong()
    val whole = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    return "${if (value < 0) "-" else ""}$$whole.${(cents % 100).toString().padStart(2, '0')}"
}
