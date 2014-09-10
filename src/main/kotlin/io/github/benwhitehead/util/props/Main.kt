/*
 * Copyright (c) 2014 Ben Whitehead.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.benwhitehead.util.props

import java.io.IOException
import java.util.ArrayList
import java.io.FileNotFoundException
import java.io.Closeable
import java.io.InputStreamReader
import java.io.FileReader
import java.io.Reader
import java.io.OutputStreamWriter
import java.util.HashMap

/**
 * @author Ben Whitehead
 */
class ExitException(val status: Int) : RuntimeException()

fun main(args: Array<String>) {
    try {
        parseArgs(args).use { params ->
            getInputStream(params.get()).use { pair ->
                val (name, reader) = pair.value
                Props(
                        name,
                        params.get().getOrElse("props", { ArrayList() }),
                        reader,
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
    val map: MutableMap<String, MutableList<String>> = HashMap()
    var i = 0
    while (i < args.size) {
        val arg = args[i]
        when (arg) {
            "-h", "--help" -> {
                usageAndExit()
            }
            "-" -> {
                map.multimapUpdate("in", "system.in", { ArrayList() })
            }
            "-f", "--file" -> {
                val filename = args[++i]
                map.multimapUpdate("files", filename, { ArrayList() })
            }
            "--version" -> {
                versionAndExit()
            }
            else -> {
                map.multimapUpdate("props", arg, { ArrayList() })
            }
        }
        i++
    }
    return ParsedArgs(map)
}

private inline fun <K, V> MutableMap<K, MutableList<V>>.multimapUpdate(key: K, value: V, default: () -> MutableList<V>) {
    val list = this.getOrElse(key, default)
    list.add(value)
    this.put(key, list)
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
        close(value)
    }

    private fun close(t: Any) {
        when (t) {
            is Closeable -> t.close()
            is Pair<*, *> -> {
                close(t.first!!)
                close(t.second!!)
            }
        }
    }
}
