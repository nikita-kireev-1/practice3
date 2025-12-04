package cli

import java.io.File
class UvmAssemblerCli(private val args: Array<String>) {
    data class AssemblerContext(
        val inputFile: String,
        val outputFile: String,
        val testMode: Boolean,
        val programText: String
    )
    fun parse(): AssemblerContext {
        if (args.isEmpty()) {
            showHelp()
            throw IllegalArgumentException("Не указаны аргументы")
        }

        var inputFile: String? = null
        var outputFile: String? = null
        var testMode = false

        var i = 0
        while (i < args.size) {
            when (args[i]) {
                "--help", "-h" -> {
                    showHelp()
                    System.exit(0)
                }
                "--test", "-t" -> {
                    testMode = true
                    i++
                }
                "--input", "-i" -> {
                    if (i + 1 < args.size) {
                        inputFile = args[i + 1]
                        i += 2
                    } else {
                        throw IllegalArgumentException("После --input должен быть указан файл")
                    }
                }
                "--output", "-o" -> {
                    if (i + 1 < args.size) {
                        outputFile = args[i + 1]
                        i += 2
                    } else {
                        throw IllegalArgumentException("После --output должен быть указан файл")
                    }
                }
                else -> {
                    when {
                        inputFile == null -> inputFile = args[i]
                        outputFile == null -> outputFile = args[i]
                        else -> println("Игнорируем неизвестный аргумент: ${args[i]}")
                    }
                    i++
                }
            }
        }
        if (inputFile == null || outputFile == null) {
            showHelp()
            throw IllegalArgumentException("Необходимо указать входной и выходной файлы")
        }
        val file = File(inputFile)
        if (!file.exists()) {
            throw IllegalArgumentException("Файл не найден: $inputFile")
        }
        val programText = file.readText()
        println("=== UVM Ассемблер (Вариант 11) ===")
        println("Входной файл: $inputFile")
        println("Выходной файл: $outputFile")
        println("Режим тестирования: ${if (testMode) "ВКЛЮЧЕН" else "ВЫКЛЮЧЕН"}")
        println("Прочитано ${programText.length} символов из файла")

        return AssemblerContext(inputFile, outputFile, testMode, programText)
    }
    private fun showHelp() {
        println("""
        UVM Ассемблер - Этап 1: Парсинг программы
        Использование:
          .\gradlew.bat run --args="path/to/<input.json> path/to/<output.bin> <option>
        Аргументы:
          <input.json>           Входной файл с программой в формате JSON
          <output.bin>           Выходной бинарный файл (для следующих этапов)
        Опции:
          -t, --test            Включить режим тестирования
          -h, --help            Показать эту справку
        Примеры:
          .\gradlew.bat run --args="src/test/resources/test_program_1.json output.bin --test"
        Формат JSON программы:
          [
            {"op": "LOAD_CONST", "reg": 29, "value": 27},
            {"op": "READ_MEM", "base_reg": 20, "dst_reg": 16, "offset": 390},
            {"op": "WRITE_MEM", "reg": 24, "addr": 178},
            {"op": "ADD", "reg": 19, "addr": 956}
          ]
        """.trimIndent())
    }
}