package commands.parser

import commands.AddCommand
import commands.LoadConstCommand
import kotlinx.serialization.json.Json
import commands.Command
import commands.ReadMemCommand
import commands.WriteMemCommand
import kotlinx.serialization.Serializable

class JsonCommandParser : CommandParser {
    @Serializable
    data class JsonCommand(
        val op: String,
        val reg: Int? = null,
        val value: Int? = null,
        val base_reg: Int? = null,
        val dst_reg: Int? = null,
        val offset: Int? = null,
        val addr: Int? = null
    )
    override fun parse(programText: String): List<Command> {
        return try {
            val jsonCommands = Json.decodeFromString<List<JsonCommand>>(programText)
            jsonCommands.map { jsonCmd ->
                when (jsonCmd.op.uppercase()) {
                    "LOAD_CONST" -> {
                        validateLoadConst(jsonCmd)
                        LoadConstCommand(
                            register = jsonCmd.reg!!,
                            constant = jsonCmd.value!!
                        )
                    }
                    "READ_MEM" -> {
                        validateReadMem(jsonCmd)
                        ReadMemCommand(
                            baseRegister = jsonCmd.base_reg!!,
                            destRegister = jsonCmd.dst_reg!!,
                            offset = jsonCmd.offset!!
                        )
                    }
                    "WRITE_MEM" -> {
                        validateWriteMem(jsonCmd)
                        WriteMemCommand(
                            sourceRegister = jsonCmd.reg!!,
                            memoryAddress = jsonCmd.addr!!
                        )
                    }
                    "ADD" -> {
                        validateAdd(jsonCmd)
                        AddCommand(
                            register = jsonCmd.reg!!,
                            memoryAddress = jsonCmd.addr!!
                        )
                    }
                    else -> throw IllegalArgumentException("Неизвестная команда: ${jsonCmd.op}")
                }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Ошибка парсинга JSON: ${e.message}")
        }
    }
    private fun validateLoadConst(cmd: JsonCommand) {
        require(cmd.reg != null) { "Поле 'reg' обязательно для LOAD_CONST" }
        require(cmd.value != null) { "Поле 'value' обязательно для LOAD_CONST" }
        require(validateField("reg", cmd.reg, 0, 31)) { "Регистр должен быть в диапазоне 0-31" }
        require(validateField("value", cmd.value, 0, 1048575)) { "Константа должна быть в диапазоне 0-1048575" }
    }
    private fun validateReadMem(cmd: JsonCommand) {
        require(cmd.base_reg != null) { "Поле 'base_reg' обязательно для READ_MEM" }
        require(cmd.dst_reg != null) { "Поле 'dst_reg' обязательно для READ_MEM" }
        require(cmd.offset != null) { "Поле 'offset' обязательно для READ_MEM" }
        require(validateField("base_reg", cmd.base_reg, 0, 31))
        require(validateField("dst_reg", cmd.dst_reg, 0, 31))
        require(validateField("offset", cmd.offset, 0, 4095))
    }
    private fun validateWriteMem(cmd: JsonCommand) {
        require(cmd.reg != null) { "Поле 'reg' обязательно для WRITE_MEM" }
        require(cmd.addr != null) { "Поле 'addr' обязательно для WRITE_MEM" }
        require(validateField("reg", cmd.reg, 0, 31))
        require(validateField("addr", cmd.addr, 0, 268435455))
    }
    private fun validateAdd(cmd: JsonCommand) {
        require(cmd.reg != null) { "Поле 'reg' обязательно для ADD" }
        require(cmd.addr != null) { "Поле 'addr' обязательно для ADD" }
        require(validateField("reg", cmd.reg, 0, 31))
        require(validateField("addr", cmd.addr, 0, 268435455))
    }
    override fun validateField(fieldName: String, value: Int, min: Int, max: Int): Boolean {
        return value in min..max
    }
}