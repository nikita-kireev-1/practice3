package assembler

import commands.Command
import commands.CommandType.Companion.fromOpcode
import model.UvmCommand


data class TestCase(
    val opcode: Int,
    val fieldB: Int?,
    val fieldC: Int?,
    val fieldD: Int?,
    val expectedHex: String
)

class InternalRepresentation {

    fun generate(commands: List<Command>): List<UvmCommand> {
        return commands.map { it.toUvmCommand() }
    }

    fun display(commands: List<UvmCommand>) {
        println("=== Внутренне представление программы ===")
        println("Всего команд: ${commands.size}")
        println("--------------------------------------------------")

        commands.forEachIndexed { index, cmd ->
            val opcodeType = fromOpcode(cmd.opcode)
            val typeName = opcodeType?.name ?: "UNKNOWN"

            println("Команда ${index + 1}: $typeName")
            println("  ${cmd.toFieldString()}")
            println()
        }
    }

    fun verifyTestCases(commands: List<UvmCommand>) {
        val testCases = listOf(
            TestCase(13, 29, 27, null, "0xDD, 0x37, 0x00, 0x00, 0x00"),
            TestCase(10, 20, 16, 390, "0xAA, 0xA1, 0x61, 0x00, 0x00"),
            TestCase(1, 24, 178, null, "0x81, 0x65, 0x01, 0x00, 0x00"),
            TestCase(2, 19, 956, null, "0x32, 0x79, 0x07, 0x00, 0x00")
        )

        commands.forEachIndexed { index, cmd ->
            val expected = testCases.getOrNull(index)
            if (expected != null) {
                val isOk = (cmd.opcode == expected.opcode) &&
                        (cmd.fieldB == expected.fieldB) &&
                        (cmd.fieldC == expected.fieldC) &&
                        (cmd.fieldD == expected.fieldD)

                if (isOk) {
                    println("Команда ${index + 1}: соответствует тесту (должна быть ${expected.expectedHex})")
                } else {
                    println("Команда ${index + 1}: не соответствует тесту")
                }
            }
        }
    }
}