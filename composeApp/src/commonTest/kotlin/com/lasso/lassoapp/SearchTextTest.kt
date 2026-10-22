package com.lasso.lassoapp

import com.lasso.lassoapp.utils.searchKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchTextTest {
    @Test
    fun matchesNamesWithOrWithoutAccentsAndCase() {
        for ((name, query) in
            listOf(
                "Rímel" to "rimel",
                "Balón mundial" to "BALON",
                "ÁÉÍÓÚÜÑ" to "aeiouun",
                "Rimel" to "RÍMEL",
                "RíMeL" to "rímel",
            )) {
            assertTrue(name.searchKey().contains(query.searchKey()), "$name / $query")
        }
        assertFalse("Labial".searchKey().contains("rimel".searchKey()))
    }

    @Test
    fun handlesCombiningAccentsWithoutChangingSpacesOrPunctuation() {
        assertEquals("rimel", "Ri\u0301mel".searchKey())
        assertEquals("balon - 2", "BALÓN - 2".searchKey())
        assertEquals("", "".searchKey())
    }
}
