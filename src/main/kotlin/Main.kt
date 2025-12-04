import assembler.BinaryEncoder
import assembler.InternalRepresentation
import cli.UvmAssemblerCli
import commands.parser.JsonCommandParser
import java.io.File
import kotlin.system.exitProcess

//[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
//.\gradlew.bat run --args="test_program.json output.bin"
//.\gradlew.bat run --args="--test test_program.json output.bin"
//.\gradlew.bat run --args="-i test_program.json -o output.bin -t"
fun main(args: Array<String>) {
    try {
        println("Обработка аргументов коммандной строки")
        val cli = UvmAssemblerCli(args)
        val context = cli.parse()
        println("Аргументы успешно обработаны")
        println("Входной файл: ${context.inputFile}")
        println("Выходной файл: ${context.outputFile}")
        println("Тестовый режим: ${if (context.testMode) "ВКЛЮЧЕН" else "ВЫКЛЮЧЕН"}")
        println("Читаем JSON и парсим его")
        val parser = JsonCommandParser()
        val commands = parser.parse(context.programText)
        println("Успешно")
        println("Найдено команд: ${commands.size}")
        println("Список команд программы:")
        println("======================================================")
        commands.forEachIndexed { index, cmd ->
            println("${index + 1}. ${cmd.description()}")
        }
        println("Создаем внутреннее представление команд")
        val irGenerator = InternalRepresentation()
        val internalRep = irGenerator.generate(commands)
        println("Внутреннее представление создано успешно")
        if (context.testMode) {
            println("=========================================================")
            println("              Тестовый режим информация                  ")
            println("=========================================================")

            irGenerator.display(internalRep)

            println("----------------------------------------------------------")
            println("Проверка соответствия тестовым примерам:")
            irGenerator.verifyTestCases(internalRep)
        }
        val binaryEncoder = BinaryEncoder()
        val binaryData = binaryEncoder.encodeToBinary(internalRep)
        val outputFile = File(context.outputFile)
        outputFile.writeBytes(binaryData)
        println("Машинный код успешно записан в файл: ${context.outputFile}")
        println("Всего ассемблировано команд: ${internalRep.size}")
        println("Размер выходного файла: ${binaryData.size} байт")
        if (context.testMode) {
            println("--- Результат ассемблирования в байтовом формате ---")
            binaryEncoder.displayBinaryOutput(binaryData)

            println("--- Проверка соответствия тестовым байтовым последовательностям ---")
            binaryEncoder.verifyBinaryTestCases(internalRep)
        }

        println("=== Ассемблирование завершено успешно! ===")

    } catch (e: IllegalArgumentException) {
        println("Ошибка с аргументами: ${e.message}")
        println("Используйте --help для справки")
        exitProcess(1)

    } catch (e: Exception) {
        println("Ошибка: ${e.message}")
        println("Детали ошибки:")
        e.printStackTrace()
        System.exit(1)
    }
}