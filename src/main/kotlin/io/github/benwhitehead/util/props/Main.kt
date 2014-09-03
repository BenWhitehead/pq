package io.github.benwhitehead.util.props

import java.io.IOException
import java.util.ArrayList
import java.io.FileNotFoundException
import java.io.Closeable
import java.util.LinkedHashMap
import java.io.InputStreamReader
import java.io.FileReader
import java.io.Reader
import java.io.OutputStreamWriter

/**
 * @author Ben Whitehead
 */
class ExitException(val status: Int) : RuntimeException()

fun main(args: Array<String>) {
    try {
        parseArgs(args).use { params ->
            getInputStream(params.get()).use { pair ->
                val t = pair.value
                Props(
                        t.first,
                        params.get().getOrElse("props", { ArrayList() }),
                        t.second,
                        OutputStreamWriter(System.out)
                ).use { props ->
                    props.run()
                }
            }
        }
    } catch (ioe: IOException) {
        System.err.print("${Props.NAME}: ")
        System.err.println(ioe.getMessage())
        System.exit(1)
    } catch (ee: ExitException) {
        System.exit(ee.status)
    } catch (iae: IllegalArgumentException) {
        System.err.print("${Props.NAME}: ")
        System.err.println(iae.getMessage())
        usage()
        System.exit(2)
    }
}

throws(javaClass<FileNotFoundException>())
fun getInputStream(params: Map<String, List<String>>): MyCloseable<Pair<String, Reader>> {
    if (params.getOrElse("in", { listOf("no") }) == listOf("system.in")) {
        return MyCloseable(Pair("stdin", InputStreamReader(System.`in`)))
    } else {
        val files = params.getOrElse("files", { ArrayList() })
        if (!files.isEmpty()) {
            val head = files.head!!
            return MyCloseable(Pair(head, FileReader(head)))
        } else {
            throw IllegalArgumentException("No input detected")
        }
    }
}


fun parseArgs(args: Array<String>): ParsedArgs {
    if (args.size == 0) {
        usageAndExit()
    }
    val map = LinkedHashMap<String, ArrayList<String>>()
    var i = 0
    while (i < args.size) {
        val arg = args[i]
        when (arg) {
            "-h", "--help" -> {
                usageAndExit()
            }
            "-" -> {
                map.getOrElse("in", { ArrayList() }).add("system.in")
            }
            "-f", "--file" -> {
                val filename = args[i + 1]
                i++
                map.getOrElse("files", { ArrayList() }).add(filename)
            }
            "--version" -> {
                versionAndExit()
            }
            else -> {
                map.getOrElse("props", { ArrayList() }).add(arg)
            }
        }
        i++
    }
    return ParsedArgs(map)
}

fun usage() {
    println("Usage: " + Props.NAME + " [--file <in>|-] [\"key=value\" | [ \"key2=value2\"]]")
    println("       -h | --help        Print this help")
    println("       -f | --file        Specify the properties files to read")
    println("       -                  Read properties from stdin")
    println("       --version          Prints version and jvm info")
}

fun usageAndExit() {
    usage()
    throw ExitException(2)
}

fun version() {
    println(Props.NAME + " " + Props.VERSION)
    println("Copyright (C) 2014 Ben Whitehead")
    println("License: Apache License v2.0")
    println()

    val sp = System.getProperties()!!
    println("java version \"${sp.getProperty("java.version")}\"")
    println("${sp.getProperty("java.runtime.name")}")
    println("${sp.getProperty("java.vm.name")} (build ${sp.getProperty("java.vm.version")}, ${sp.getProperty("java.vm.info")})%n")
}

fun versionAndExit() {
    version()
    throw ExitException(0)
}

class ParsedArgs(map: Map<String, List<String>>) : Closeable {
    private val map: MyCloseable<Map<String, List<String>>> = MyCloseable(map)
    public fun get(): Map<String, List<String>> {
        return map.value
    }

    throws(javaClass<IOException>())
    override fun close() {
        map.close()
    }
}

class MyCloseable<T>(val value: T) : Closeable {
    throws(javaClass<IOException>())
    override fun close() {
        if (value is Closeable) {
            value.close()
        } else if (value is Pair<*, *>) {
            val first = value.second
            if (first is Closeable) {
                first.close()
            }
            val second = value.second
            if (second is Closeable) {
                second.close()
            }
        }
    }
}
