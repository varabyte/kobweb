package com.varabyte.kobweb.gradle.core.project.common

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import java.io.File

object PsiUtils {
    // For now, we're directly parsing Kotlin code using the embedded Kotlin compiler. This is a temporary approach.
    // In the future, this should use KSP to navigate through source files. See also: Bug #4
    fun createKotlinProject(): Project {
        return KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.JS_CONFIG_FILES
        ).project
    }
}

fun Project.parseKotlinFile(file: File): KtFile {
    return PsiManager.getInstance(this)
        .findFile(
            LightVirtualFile(
                file.name,
                KotlinFileType.INSTANCE,
                // Standardize newlines or else PSI parsing gets confused (e.g. by \r on Windows)
                file.readText().replace(System.lineSeparator(), "\n"),
            )
        ) as KtFile
}

fun KtCallElement.getStringValue(index: Int): String? {
    val strExpr = valueArguments.getOrNull(index)?.getArgumentExpression() as? KtStringTemplateExpression ?: return null
    return strExpr.entries.firstOrNull()?.text
}
