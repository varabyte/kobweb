package com.varabyte.kobweb.silk.components.style

// TODO: docs
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class CssName(val name: String)

// TODO: docs
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class CssPrefix(val prefix: String)
