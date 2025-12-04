package model

data class UvmCommand(
    val opcode: Int,      // Поле A - код операции
    val fieldB: Int? = null,  // Поле B (если есть)
    val fieldC: Int? = null,  // Поле C (если есть)
    val fieldD: Int? = null   // Поле D (если есть)
) {
    fun toFieldString(): String {
        val parts = mutableListOf("A=$opcode")

        if (fieldB != null) parts.add("B=$fieldB")
        if (fieldC != null) parts.add("C=$fieldC")
        if (fieldD != null) parts.add("D=$fieldD")

        return parts.joinToString(", ")
    }
}