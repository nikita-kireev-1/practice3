package assembler

import model.UvmCommand

class BinaryEncoder {

    data class BinaryTestCase(
        val opcode: Int,
        val fieldB: Int?,
        val fieldC: Int?,
        val fieldD: Int?,
        val expectedBytes: ByteArray
    )

    private fun encodeCommand(cmd: UvmCommand): ByteArray {
        val result = ByteArray(5) { 0 }

        when (cmd.opcode) {
            // LOAD_CONST: A=13 (4 бита), B (5 бит), C (20 бит)
            13 -> {
                var packed: Long = 0
                packed = packed or (cmd.opcode.toLong() and 0xF)
                packed = packed or ((cmd.fieldB!!.toLong() and 0x1F) shl 4)
                packed = packed or ((cmd.fieldC!!.toLong() and 0xFFFFFL) shl 9)
                for (i in 0 until 5) {
                    result[i] = ((packed shr (i * 8)) and 0xFF).toByte()
                }
            }

            // READ_MEM: A=10 (4 бита), B (5 бит), C (5 бит), D (12 бит)
            10 -> {
                var packed: Long = 0
                packed = packed or (cmd.opcode.toLong() and 0xF)
                packed = packed or ((cmd.fieldB!!.toLong() and 0x1F) shl 4)
                packed = packed or ((cmd.fieldC!!.toLong() and 0x1F) shl 9)
                packed = packed or ((cmd.fieldD!!.toLong() and 0xFFF) shl 14)
                for (i in 0 until 5) {
                    result[i] = ((packed shr (i * 8)) and 0xFF).toByte()
                }
            }

            // WRITE_MEM: A=1 (4 бита), B (5 бит), C (28 бит)
            1 -> {
                var packed: Long = 0
                packed = packed or (cmd.opcode.toLong() and 0xF)
                packed = packed or ((cmd.fieldB!!.toLong() and 0x1F) shl 4)
                packed = packed or ((cmd.fieldC!!.toLong() and 0xFFFFFFFL) shl 9)
                for (i in 0 until 5) {
                    result[i] = ((packed shr (i * 8)) and 0xFF).toByte()
                }
            }

            // ADD: A=2 (4 бита), B (5 бит), C (28 бит)
            2 -> {
                var packed: Long = 0
                packed = packed or (cmd.opcode.toLong() and 0xF)
                packed = packed or ((cmd.fieldB!!.toLong() and 0x1F) shl 4)
                packed = packed or ((cmd.fieldC!!.toLong() and 0xFFFFFFFL) shl 9)
                for (i in 0 until 5) {
                    result[i] = ((packed shr (i * 8)) and 0xFF).toByte()
                }
            }

            else -> throw IllegalArgumentException("Неизвестный код операции: ${cmd.opcode}")
        }

        return result
    }

    fun encodeToBinary(commands: List<UvmCommand>): ByteArray {
        val result = mutableListOf<Byte>()

        commands.forEach { cmd ->
            val encoded = encodeCommand(cmd)
            result.addAll(encoded.toList())
        }

        return result.toByteArray()
    }

    fun displayBinaryOutput(binaryData: ByteArray) {
        println("Байтовое представление программы:")
        println("Всего байт: ${binaryData.size}")
        println("--------------------------------------------------")

        val commandsCount = binaryData.size / 5

        for (i in 0 until commandsCount) {
            print("Команда ${i + 1}: ")
            val bytes = mutableListOf<String>()

            for (j in 0 until 5) {
                val byte = binaryData[i * 5 + j]
                bytes.add(String.format("0x%02X", byte.toInt() and 0xFF))
            }

            println(bytes.joinToString(", "))
        }
    }

    fun verifyBinaryTestCases(commands: List<UvmCommand>) {
        val testCases = listOf(
            BinaryTestCase(13, 29, 27, null,
                byteArrayOf(0xDD.toByte(), 0x37, 0x00, 0x00, 0x00)),
            BinaryTestCase(10, 20, 16, 390,
                byteArrayOf(0x4A.toByte(), 0xA1.toByte(), 0x61, 0x00, 0x00)),
            BinaryTestCase(1, 24, 178, null,
                byteArrayOf(0x81.toByte(), 0x65, 0x01, 0x00, 0x00)),
            BinaryTestCase(2, 19, 956, null,
                byteArrayOf(0x32, 0x79, 0x07, 0x00, 0x00))
        )

        commands.forEachIndexed { index, cmd ->
            val expected = testCases.getOrNull(index)
            if (expected != null) {
                val actual = encodeCommand(cmd)
                val isOk = actual.contentEquals(expected.expectedBytes)

                if (isOk) {
                    println("Команда ${index + 1}: байты совпадают с тестом")
                    print("  Ожидалось: ")
                    println(expected.expectedBytes.joinToString(", ") {
                        String.format("0x%02X", it.toInt() and 0xFF)
                    })
                } else {
                    println("Команда ${index + 1}: байты НЕ совпадают с тестом")
                    print("  Ожидалось: ")
                    println(expected.expectedBytes.joinToString(", ") {
                        String.format("0x%02X", it.toInt() and 0xFF)
                    })
                    print("  Получено:  ")
                    println(actual.joinToString(", ") {
                        String.format("0x%02X", it.toInt() and 0xFF)
                    })
                }
            }
        }
    }
}