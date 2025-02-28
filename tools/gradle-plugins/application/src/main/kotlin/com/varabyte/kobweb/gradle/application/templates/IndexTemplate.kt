package com.varabyte.kobweb.gradle.application.templates

import com.varabyte.kobweb.gradle.application.BuildTarget
import kotlinx.html.BODY
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.dom.document
import kotlinx.html.dom.serialize
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

private fun BODY.buildIndicator() {
    unsafe {
        // language=html
        raw(
            """
                <!-- Encoded spinner character is a cobweb -->
                <div id="status"><span id="warning">❌</span><span id="spinner">🕸️</span> <span id="text"></span>
                    <style>
                        @keyframes kobweb-spin {
                            from { transform: rotate(0deg); }
                            to { transform: rotate(359deg); }
                        }
                        body > #status {
                            position: fixed;
                            font-size: 24px;
                            color: black;
                            background-color: whitesmoke;
                            top: 20px;
                            left: 50%;
                            transform: translateX(-50%);
                            padding: 10px;
                            border: 1px solid;
                            border-radius: 10px;
                            visibility: hidden;
                            opacity: 0;
                            z-index: 2147483647;
                            user-select: none;
                        }
                        body > #status > .hidden {
                           display: none;
                        }
                        body > #status > .visible {
                            display: inline-block;
                        }
                        body > #status.fade-in {
                            visibility: visible;
                            opacity: 1;
                            transition: opacity 1s;
                        }
                        body > #status.fade-out {
                           visibility: hidden;
                           opacity: 0;
                           transition: visibility 0s 1s, opacity 1s;
                        }
                        body > #status > #spinner {
                            animation: kobweb-spin 1.5s linear infinite;
                        }
                    </style>
                </div>
            """.trimIndent()
        )
    }
}

fun createIndexFile(
    title: String,
    lang: String,
    headElements: Iterable<String>,
    src: String,
    scriptAttributes: Map<String, String>,
    buildTarget: BuildTarget
): String {
    return document {
        append {
            html {
                if (lang.isNotEmpty()) {
                    this.lang = lang
                }

                head {
                    title(content = title)
                    headElements.forEach { elements ->
                        unsafe { raw(elements) }
                    }
                }

                body {
                    div {
                        id = "_kobweb-root"
                        // Fill max size just in case user sets html / body size
                        style = "width: 100%; height: 100%;"
                    }

                    if (buildTarget == BuildTarget.DEBUG) {
                        buildIndicator()
                    }

                    script {
                        this.src = src
                        attributes.putAll(scriptAttributes)
                    }
                }
            }
        }
    }.serialize()
}
