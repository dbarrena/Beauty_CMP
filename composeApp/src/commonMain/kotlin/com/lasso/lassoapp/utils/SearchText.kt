package com.lasso.lassoapp.utils

/** Fold Latin accents for matching only; keep the original text for display. */
internal fun String.searchKey(): String = buildString {
    for (character in this@searchKey.lowercase()) {
        when (character) {
            'á',
            'à',
            'â',
            'ä',
            'ã',
            'å' -> append('a')
            'é',
            'è',
            'ê',
            'ë' -> append('e')
            'í',
            'ì',
            'î',
            'ï' -> append('i')
            'ó',
            'ò',
            'ô',
            'ö',
            'õ' -> append('o')
            'ú',
            'ù',
            'û',
            'ü' -> append('u')
            'ñ' -> append('n')
            'ç' -> append('c')
            'ý',
            'ÿ' -> append('y')
            else -> if (character.category !in searchCombiningMarks) append(character)
        }
    }
}

private val searchCombiningMarks =
    setOf(
        CharCategory.NON_SPACING_MARK,
        CharCategory.COMBINING_SPACING_MARK,
        CharCategory.ENCLOSING_MARK,
    )
