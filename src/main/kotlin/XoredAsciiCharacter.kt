import kotlin.experimental.xor

data class XoredAsciiCharacter(val char1: Byte, val char2: Byte, val xored: Byte) {

    constructor(char1: Byte, char2: Byte) : this(char1, char2, char1 xor char2)
}