import assembler.InternalRepresentation
import cli.UvmAssemblerCli
import commands.parser.JsonCommandParser
import java.io.File
import kotlin.system.exitProcess

//[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
//.\gradlew.bat run --args="src/test/resources/test_program_1.json output.bin --test"
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