package commands

enum class CommandType(val opcode: Int, val description: String) {
    LOAD_CONST(13, "Загрузка константы в регистр"),
    READ_MEM(10, "Чтение значения из памяти в регистр"),
    WRITE_MEM(1, "Запись значения из регистра в память"),
    ADD(2, "Сложение регистра с значением из памяти");

    companion object {
        fun fromOpcode(opcode: Int): CommandType? {
            return values().find { it.opcode == opcode }
        }
    }
}