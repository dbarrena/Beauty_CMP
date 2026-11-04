package com.lasso.lassoapp.utils

/** Small PDF 1.4 writer with embedded TrueType fonts and selectable Unicode text.
 * Keeping this platform independent makes Android, iOS and the preview identical.
 */
internal class PdfTrueTypeFont(val bytes: ByteArray, val name: String) {
    private fun u16(p: Int) = ((bytes[p].toInt() and 255) shl 8) or (bytes[p + 1].toInt() and 255)
    private fun i16(p: Int) = u16(p).toShort().toInt()
    private fun u32(p: Int) = (u16(p) shl 16) or u16(p + 2)
    private val tables = (0 until u16(4)).associate {
        val p = 12 + it * 16
        bytes.copyOfRange(p, p + 4).decodeToString() to u32(p + 8)
    }
    private val units = u16(tables.getValue("head") + 18)
    private val metrics = u16(tables.getValue("hhea") + 34)
    private val cmap: Int = run {
        val base = tables.getValue("cmap")
        (0 until u16(base + 2)).map { base + u32(base + 4 + it * 8 + 4) }
            .first { u16(it) == 4 }
    }
    private val used = linkedMapOf<Int, Char>()
    private fun glyph(char: Char): Int {
        val segments = u16(cmap + 6) / 2
        val ends = cmap + 14
        val starts = ends + 2 * segments + 2
        val deltas = starts + 2 * segments
        val ranges = deltas + 2 * segments
        val c = char.code
        for (i in 0 until segments) {
            if (c > u16(ends + i * 2)) continue
            if (c < u16(starts + i * 2)) return 0
            val delta = i16(deltas + i * 2)
            val offset = u16(ranges + i * 2)
            if (offset == 0) return (c + delta) and 65535
            val g = u16(ranges + i * 2 + offset + (c - u16(starts + i * 2)) * 2)
            return if (g == 0) 0 else (g + delta) and 65535
        }
        return 0
    }
    private fun advance(g: Int) = u16(tables.getValue("hmtx") + minOf(g, metrics - 1) * 4) * 1000 / units
    fun width(text: String) = text.sumOf { advance(glyph(it)) }
    fun encode(text: String) = text.map {
        val g = glyph(it)
        used[g] = if (g == 0) '?' else it
        g.toString(16).padStart(4, '0')
    }.joinToString("")

    fun addTo(pdf: PdfObjects): Int {
        val file = pdf.stream("/Length1 ${bytes.size}", bytes)
        val head = tables.getValue("head")
        val box = (0..3).joinToString(" ") { (i16(head + 36 + it * 2) * 1000 / units).toString() }
        val hhea = tables.getValue("hhea")
        val descriptor = pdf.add("<< /Type /FontDescriptor /FontName /$name /Flags 32 /FontBBox [$box] /ItalicAngle 0 /Ascent ${i16(hhea+4)*1000/units} /Descent ${i16(hhea+6)*1000/units} /CapHeight 730 /StemV 80 /FontFile2 $file 0 R >>")
        val widths = used.keys.joinToString(" ") { "$it [${advance(it)}]" }
        val cid = pdf.add("<< /Type /Font /Subtype /CIDFontType2 /BaseFont /$name /CIDSystemInfo << /Registry (Adobe) /Ordering (Identity) /Supplement 0 >> /FontDescriptor $descriptor 0 R /CIDToGIDMap /Identity /DW 1000 /W [$widths] >>")
        val mapping = buildString {
            append("/CIDInit /ProcSet findresource begin 12 dict begin begincmap /CIDSystemInfo << /Registry (Adobe) /Ordering (UCS) /Supplement 0 >> def /CMapName /Adobe-Identity-UCS def /CMapType 2 def 1 begincodespacerange <0000> <FFFF> endcodespacerange\n")
            used.entries.chunked(100).forEach { chunk ->
                append("${chunk.size} beginbfchar\n")
                chunk.forEach { (g, c) -> append("<${g.toString(16).padStart(4,'0')}> <${c.code.toString(16).padStart(4,'0')}>\n") }
                append("endbfchar\n")
            }
            append("endcmap CMapName currentdict /CMap defineresource pop end end")
        }
        val unicode = pdf.stream("", mapping.encodeToByteArray())
        return pdf.add("<< /Type /Font /Subtype /Type0 /BaseFont /$name /Encoding /Identity-H /DescendantFonts [$cid 0 R] /ToUnicode $unicode 0 R >>")
    }
}

internal class PdfObjects {
    private val objects = mutableListOf<ByteArray>()
    fun add(value: String) = add(value.encodeToByteArray())
    private fun add(value: ByteArray): Int { objects += value; return objects.size }
    fun stream(dictionary: String, bytes: ByteArray) = add(
        "<< $dictionary /Length ${bytes.size} >>\nstream\n".encodeToByteArray() + bytes + "\nendstream".encodeToByteArray()
    )
    fun reserve() = add("")
    fun set(id: Int, value: String) { objects[id - 1] = value.encodeToByteArray() }
    fun finish(root: Int): ByteArray {
        val chunks = mutableListOf("%PDF-1.4\n%Lasso\n".encodeToByteArray())
        var size = chunks.first().size
        val offsets = mutableListOf(0)
        objects.forEachIndexed { i, bytes ->
            offsets += size
            val chunk = "${i+1} 0 obj\n".encodeToByteArray() + bytes + "\nendobj\n".encodeToByteArray()
            chunks += chunk; size += chunk.size
        }
        chunks += buildString {
            append("xref\n0 ${objects.size+1}\n0000000000 65535 f \n")
            offsets.drop(1).forEach { append("${it.toString().padStart(10,'0')} 00000 n \n") }
            append("trailer\n<< /Size ${objects.size+1} /Root $root 0 R >>\nstartxref\n$size\n%%EOF\n")
        }.encodeToByteArray()
        val result = ByteArray(chunks.sumOf { it.size })
        var offset = 0
        chunks.forEach { it.copyInto(result, offset); offset += it.size }
        return result
    }
}

internal fun buildPdf(pages: List<String>, fonts: List<PdfTrueTypeFont>, logo: ByteArray): ByteArray {
    val pdf = PdfObjects()
    val catalog = pdf.reserve()
    val tree = pdf.reserve()
    val fontIds = fonts.map { it.addTo(pdf) }
    val image = pdf.stream("/Type /XObject /Subtype /Image /Width 720 /Height 198 /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode", logo)
    val ids = pages.map { page ->
        val content = pdf.stream("", page.encodeToByteArray())
        pdf.add("<< /Type /Page /Parent $tree 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 ${fontIds[0]} 0 R /F2 ${fontIds[1]} 0 R >> /XObject << /Logo $image 0 R >> >> /Contents $content 0 R >>")
    }
    pdf.set(tree, "<< /Type /Pages /Count ${ids.size} /Kids [${ids.joinToString(" ") { "$it 0 R" }}] >>")
    pdf.set(catalog, "<< /Type /Catalog /Pages $tree 0 R >>")
    return pdf.finish(catalog)
}
