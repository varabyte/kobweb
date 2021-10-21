package com.varabyte.kobweb.cli.common

import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.input.Completions
import com.varabyte.konsole.foundation.input.input
import com.varabyte.konsole.foundation.input.onInputChanged
import com.varabyte.konsole.foundation.input.onInputEntered
import com.varabyte.konsole.foundation.input.runUntilInputEntered
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.render.aside
import com.varabyte.konsole.foundation.text.black
import com.varabyte.konsole.foundation.text.bold
import com.varabyte.konsole.foundation.text.cyan
import com.varabyte.konsole.foundation.text.green
import com.varabyte.konsole.foundation.text.invert
import com.varabyte.konsole.foundation.text.red
import com.varabyte.konsole.foundation.text.text
import com.varabyte.konsole.foundation.text.textLine
import com.varabyte.konsole.foundation.text.yellow
import com.varabyte.konsole.runtime.KonsoleApp
import com.varabyte.konsole.runtime.KonsoleBlock.RunScope
import com.varabyte.konsole.runtime.render.RenderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

private enum class ProcessingState {
    IN_PROGRESS,
    FAILED,
    SUCCEEDED
}

fun RenderScope.textError(text: String) {
    red { text("✗") }
    text(' ')
    textLine(text)
}

fun KonsoleApp.processing(message: String, blockingWork: () -> Unit): Boolean {
    val spinner = konsoleAnimOf(Anims.SPINNER)
    val ellipsis = konsoleAnimOf(Anims.ELLIPSIS)
    var state by konsoleVarOf(ProcessingState.IN_PROGRESS)
    konsole {
        when (state) {
            ProcessingState.IN_PROGRESS -> yellow { text(spinner) }
            ProcessingState.FAILED -> red { text("✗") }
            ProcessingState.SUCCEEDED -> green { text("✓") }
        }

        text(' ')
        text(message)

        when (state) {
            ProcessingState.IN_PROGRESS -> text(ellipsis)
            ProcessingState.FAILED -> textLine("${Anims.ELLIPSIS.frames.last()} Failed.")
            ProcessingState.SUCCEEDED -> textLine("${Anims.ELLIPSIS.frames.last()} Done!")
        }
    }.run {
        state = try {
            blockingWork()
            ProcessingState.SUCCEEDED
        } catch (ex: Exception) {
            ex.printStackTrace()
            ProcessingState.FAILED
        }
    }

    return state == ProcessingState.SUCCEEDED
}

fun KonsoleApp.informError(message: String) {
    konsole {
        textError(message)
    }.run()
}

fun KonsoleApp.informInfo(message: String) {
    konsole {
        yellow { text('!') }
        text(' ')
        textLine(message)
    }.run()
}

/**
 * @param validateAnswer Take a string (representing a user's answer), returning a new string which represents an error
 *   message, or null if no error.
 */
fun KonsoleApp.queryUser(
    query: String,
    defaultAnswer: String?,
    validateAnswer: (String) -> String? = Validations::isNotEmpty
): String {
    var answer by konsoleVarOf("")
    var error by konsoleVarOf<String?>(null)
    konsole {
        cyan { text('?') }
        text(' ')
        bold { textLine("$query ") }
        text("> ")
        if (answer.isNotEmpty()) {
            textLine(answer)
        }
        else {
            input(defaultAnswer?.let { Completions(it) })
            textLine()
            error?.let { error ->
                scopedState {
                    red()
                    invert()
                    textLine(error)
                }
            }
        }
        textLine()
    }.runUntilInputEntered {
        lateinit var possibleAnswer: String
        fun validateInput(input: String) {
            possibleAnswer = input.takeIf { it.isNotBlank() } ?: defaultAnswer.orEmpty()
            error = validateAnswer(possibleAnswer)
        }
        validateInput("")
        onInputChanged { validateInput(input) }
        onInputEntered {
            if (error == null) {
                answer = possibleAnswer
            }
            else {
                rejectInput()
            }
        }
    }
    return answer
}

/**
 * Convenience method for adding a single line, useful to do before or after queries or information messages.
*/
fun KonsoleApp.newline() {
    konsole { textLine() }.run()
}

fun RunScope.consumeStream(stream: InputStream, isError: Boolean, onLineRead: () -> Unit) {
    val isr = InputStreamReader(stream)
    val br = BufferedReader(isr)
    while (true) {
        val line = br.readLine() ?: break
        onLineRead()
        aside {
            if (isError) red() else black(isBright = true)
            textLine(line)
        }
    }
}

fun RunScope.consumeProcessOutput(process: Process, onLineRead: () -> Unit = {}) {
    CoroutineScope(Dispatchers.IO).launch { consumeStream(process.inputStream, isError = false, onLineRead) }
    CoroutineScope(Dispatchers.IO).launch { consumeStream(process.errorStream, isError = true, onLineRead) }
}