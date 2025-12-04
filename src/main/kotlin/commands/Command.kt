package commands

import model.UvmCommand

sealed interface Command {
    fun toUvmCommand(): UvmCommand
    fun description(): String
}
data class LoadConstCommand(
    val register: Int,  // Адрес регистра (поле B)
    val constant: Int   // Константа (поле C)
) : Command {
    override fun toUvmCommand(): UvmCommand {
        return UvmCommand(
            opcode = CommandType.LOAD_CONST.opcode,
            fieldB = register,
            fieldC = constant
        )
    }
    override fun description(): String {
        return "LOAD_CONST: загрузить константу $constant в регистр R$register"
    }
}

data class ReadMemCommand(
    val baseRegister: Int,   // Базовый регистр (поле B)
    val destRegister: Int,   // Регистр назначения (поле C)
    val offset: Int          // Смещение (поле D)
) : Command {
    override fun toUvmCommand(): UvmCommand {
        return UvmCommand(
            opcode = CommandType.READ_MEM.opcode,
            fieldB = baseRegister,
            fieldC = destRegister,
            fieldD = offset
        )
    }
    override fun description(): String {
        return "READ_MEM: прочитать из памяти [R$baseRegister + $offset] в регистр R$destRegister"
    }
}

data class WriteMemCommand(
    val sourceRegister: Int, // Регистр-источник (поле B)
    val memoryAddress: Int   // Адрес в памяти (поле C)
) : Command {
    override fun toUvmCommand(): UvmCommand {
        return UvmCommand(
            opcode = CommandType.WRITE_MEM.opcode,
            fieldB = sourceRegister,
            fieldC = memoryAddress
        )
    }
    override fun description(): String {
        return "WRITE_MEM: записать из регистра R$sourceRegister в память по адресу $memoryAddress"
    }
}
data class AddCommand(
    val register: Int,       // Регистр (поле B)
    val memoryAddress: Int   // Адрес в памяти (поле C)
) : Command {
    override fun toUvmCommand(): UvmCommand {
        return UvmCommand(
            opcode = CommandType.ADD.opcode,
            fieldB = register,
            fieldC = memoryAddress
        )
    }
    override fun description(): String {
        return "ADD: сложить регистр R$register со значением из памяти [$memoryAddress], результат в R$register"
    }
}