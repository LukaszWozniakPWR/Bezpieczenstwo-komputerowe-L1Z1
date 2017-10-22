import com.google.gson.Gson

class Main

fun main(args: Array<String>) {
    val cryptograms = Gson().fromJson("cryptograms.json".asStringResource(), Array<Array<Int>>::class.java)
            .map { it.toList().map { it.toString(10).toInt(2).toByte() } }
            .toList()

    val decryptor = AsciiXorCipherDecryptor(cryptograms)
    cryptograms.forEachIndexed { i, _ -> println(decryptor.decryptedCryptogramWithNumber(i)) }
}

fun String.asStringResource() = Main::class.java.getResource(this).readText()