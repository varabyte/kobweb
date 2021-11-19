package com.varabyte.kobweb.core

/**
 * An annotation which identifies a function as one which will be called when the page opens before nodes are liad out.
 * The function should take an [InitKobwebContext] as its only parameter.
 */
annotation class InitKobweb