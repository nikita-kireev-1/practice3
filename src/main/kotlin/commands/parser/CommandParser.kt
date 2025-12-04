package commands.parser

import commands.Command
interface CommandParser {
    fun parse(programText: String): List<Command>
    fun validateField(fieldName: String, value: Int, min: Int, max: Int): Boolean
}