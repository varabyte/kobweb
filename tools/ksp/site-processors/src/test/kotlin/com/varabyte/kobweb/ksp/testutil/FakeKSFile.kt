package com.varabyte.kobweb.ksp.testutil

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSVisitor
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.NonExistLocation
import com.google.devtools.ksp.symbol.Origin

/**
 * A [KSFile] with just a package and a file name, for testing code which only cares about where a file is.
 *
 * @param pkg The file's package, e.g. "com.example.pages.blog"
 * @param fileName The file's name, e.g. "Post.kt"
 */
class FakeKSFile(pkg: String, override val fileName: String) : KSFile {
    override val packageName: KSName = object : KSName {
        override fun asString() = pkg
        override fun getQualifier() = pkg.substringBeforeLast('.', missingDelimiterValue = "")
        override fun getShortName() = pkg.substringAfterLast('.')
    }
    override val filePath get() = "/src/${packageName.asString().replace('.', '/')}/$fileName"

    override val declarations = emptySequence<KSDeclaration>()
    override val annotations = emptySequence<KSAnnotation>()
    override val origin = Origin.KOTLIN
    override val location: Location = NonExistLocation
    override val parent: KSNode? = null
    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R = visitor.visitFile(this, data)
}
