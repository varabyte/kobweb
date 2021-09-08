package com.varabyte.kobweb.cli.common

import com.varabyte.konsole.foundation.input.*
import com.varabyte.konsole.foundation.konsoleVarOf
import com.varabyte.konsole.foundation.text.*
import com.varabyte.konsole.runtime.KonsoleApp

/**
 * @param validateAnswer Take a string (representing a user's answer), returning a new string which represents an error
 *   message, or null if no error.
 */
fun KonsoleApp.queryUser(query: String, defaultAnswer: String, validateAnswer: (String) -> String? = { null }): String {
    var answer by konsoleVarOf("")
    var error by konsoleVarOf<String?>(null)
    konsole {
        cyan { text('?') }
        text(' ')
        bold { text("$query ") }
        if (answer.isNotEmpty()) {
            textLine(answer)
        }
        else {
            input(Completions(defaultAnswer))
            textLine()
            error?.let { error ->
                scopedState {
                    red()
                    invert()
                    textLine(error)
                }
            }
        }
    }.runUntilInputEntered {
        lateinit var possibleAnswer: String
        fun validateInput(input: String) {
            possibleAnswer = input.takeIf { it.isNotBlank() } ?: defaultAnswer
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