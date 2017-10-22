import kotlin.experimental.xor

class AsciiXorCipherDecryptor(
        private val cryptograms: List<List<Byte>>,
        private val allowedCharacters: List<Byte> = DEFAULT_ALLOWED_CHARACTERS
) {

    private val xoredAllowedCharacters = generateXoredCharactersList()
    private val key = arrayOfNulls<Byte?>(cryptograms.maxBy { it.size }?.size ?: 0)

    init {
        val keyOccurrences = mutableListOf<KeyOccurrence>()

        cryptograms.forEach { cryptogram ->
            filterSuitableCryptogramsFor(cryptogram).forEach { suitableCryptogram ->
                cryptogram.forEachIndexed { index, char1 ->
                    val char2 = suitableCryptogram[index]
                    val xoredValue = char1 xor char2
                    val xoredAllowedCharacter = xoredAllowedCharacters.find { it.xored == xoredValue }
                    if (xoredAllowedCharacter != null) {
                        listOf<Byte>(
                                char1 xor xoredAllowedCharacter.char1,
                                char1 xor xoredAllowedCharacter.char2,
                                char2 xor xoredAllowedCharacter.char1,
                                char2 xor xoredAllowedCharacter.char2
                        ).forEach { key ->
                            val keyOccurrence = keyOccurrences.find { it.key == key && it.index == index }
                            if (keyOccurrence == null) {
                                keyOccurrences.add(KeyOccurrence(key, index))
                            } else {
                                ++keyOccurrence.occurrences
                            }
                        }
                    }
                }
            }
        }
        populateKeyArrayWithMostlyOccurredKeys(keyOccurrences)

    }

    fun decryptedCryptogramWithNumber(number: Int): String {
        return mutableListOf<Byte>().apply {
            cryptograms[number].forEachIndexed { index, byte ->
                if (key[index] != null) {
                    add(byte xor key[index]!!)
                } else {
                    add('x'.toByte())
                }
            }
        }.toByteArray().toString(Charsets.UTF_8)
    }

    private fun populateKeyArrayWithMostlyOccurredKeys(keyOccurrences: MutableList<KeyOccurrence>) {
        for (i in 0 until key.size) {
            val occurrencesWithCurrentIndex = keyOccurrences.filter { it.index == i }
            key[i] = occurrencesWithCurrentIndex.maxBy { it.occurrences }?.key
            keyOccurrences.removeAll(occurrencesWithCurrentIndex)
        }
    }

    private fun generateXoredCharactersList(): List<XoredAsciiCharacter> {
        return mutableListOf<XoredAsciiCharacter>().apply {
            allowedCharacters.forEach { char1 ->
                allowedCharacters.minus(char1).forEach { char2 ->
                    add(XoredAsciiCharacter(char1, char2))
                }
            }
        }
    }

    private fun filterSuitableCryptogramsFor(cryptogram: List<Byte>): List<List<Byte>>
            = cryptograms.minusElement(cryptogram).filter { it.size >= cryptogram.size }

    companion object {
        val DEFAULT_ALLOWED_CHARACTERS = "abcdefghijklmnoprstuwyz ".toByteArray(Charsets.UTF_8).toList()

    }
}