package com.varabyte.kobweb.cli.common

import com.varabyte.konsole.foundation.anim.konsoleAnimOf
import com.varabyte.konsole.foundation.input.*
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.text.*
import com.varabyte.konsole.runtime.KonsoleApp
import com.varabyte.konsole.runtime.render.RenderScope

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