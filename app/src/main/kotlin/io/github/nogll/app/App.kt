package io.github.nogll.app

import io.github.nogll.bencode.Parser
import io.github.nogll.bencode.model.BDict
import io.github.nogll.bencode.model.BElement
import io.github.nogll.bencode.model.BInt
import io.github.nogll.bencode.model.BList
import io.github.nogll.bencode.model.BString
import java.nio.file.Files
import kotlin.io.path.Path

fun BElement.getTypeName() = when (this) {
    is BInt -> "Int"
    is BList -> "List"
    is BDict -> "Dict"
    is BString -> "String"
}

fun getElementByName(root: BElement, name: String): BElement? {
    return when (root) {
        is BDict -> root.map[BString(name)]
        is BList -> name.toIntOrNull()?.let { root.list.getOrNull(it) }
        else -> null
    }
}

fun getKeys(el: BElement): List<String>? {
    return when (el) {
        is BList -> el.list.indices.asSequence()
            .map { "$it -> ${el.list[it].getTypeName()}" }.toList()
        is BDict -> el.map.entries
            .map { entry -> "\"${entry.key.asString()}\" -> ${entry.value.getTypeName()}" }
            .toList()
        else -> null
    }
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Для запуска передайте путь до файла")
    }
    val path = Path(args[0])
    val bytes = Files.readAllBytes(path)
    val parser = Parser()
    val rootElement = parser.parse(bytes)

    val currentPath = mutableListOf<String>()
    val elements = mutableListOf(rootElement)

    print("/: ")
    var op: String = readln()
    while (op != "quit") {
        val parts = op.trim().split(" ", limit = 2)
        val command = parts.getOrNull(0) ?: ""
        val commandArgs = parts.getOrNull(1)

        when (command) {
            "cd" -> {
                when {
                    commandArgs == null -> println("Команда ожидала аргументы")

                    commandArgs == ".." && currentPath.isNotEmpty() -> {
                        currentPath.removeLast()
                        elements.removeLast()
                    }

                    else -> getElementByName(elements.last(), commandArgs)?.also { el ->
                        currentPath.add(commandArgs)
                        elements.add(el)
                    } ?: println("Не верный ключ")
                }
            }

            "ls" -> {
                getKeys(elements.last())?.forEach { println(it) }
            }

            "cat" -> {
                val el = elements.last()
                when (el) {
                    is BInt -> println("Int: ${el.num}")
                    is BString -> println("String: ${el.asString()}")
                    is BDict -> println("Dict")
                    is BList -> println("List with ${el.list.size} element(s)")
                }
            }

            "help" -> {
                println("cd <key>       - перейти в элемент словаря/списка")
                println("cd ..          - вернуться на уровень выше")
                println("ls             - показать ключи текущего элемента")
                println("cat            - показать значение текущего элемента")
                println("quit           - выйти")
            }

        }

        print("/" + currentPath.joinToString("/") + ": ")
        op = readln()
    }
}
