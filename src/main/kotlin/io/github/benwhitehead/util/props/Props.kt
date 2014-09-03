package io.github.benwhitehead.util.props

import java.io.Closeable
import kotlin.properties.Delegates
import java.util.Properties
import java.io.InputStreamReader
import java.io.Reader
import java.io.Writer
import java.io.StringReader


data class Props(
        val inputSource: String,
        val props: List<String>,
        val input: Reader,
        val output: Writer
) : Closeable {
    class object {
        val NAME = "props"
        val VERSION by Delegates.lazy {
            val sys = Properties()
            sys.load(InputStreamReader(javaClass<Props>().getResourceAsStream("/build.properties")!!))
            sys.getProperty("version")!!
        }
    }

    fun run() {
        val properties = Properties()
        properties.load(input)

        val sb = StringBuilder()
        for (arg in props) {
            sb.append(arg).append("\n")
        }

        properties.load(StringReader(sb.toString()))

        properties.store(output, "Generated by ${NAME} v${VERSION} from $inputSource")
    }

    override fun close() {
        input.close()
        output.flush()
        output.close()
    }
}
