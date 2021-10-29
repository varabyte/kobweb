package com.varabyte.kobweb.gradle.application.extensions

import org.jetbrains.kotlin.com.intellij.psi.PsiElement

fun PsiElement.visitAllChildren(visit: (PsiElement) -> Unit) {
    visit(this)
    children.forEach { it.visitAllChildren(visit) }
}