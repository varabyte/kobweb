package nekt.core

/**
 * A slug is the part of the URL that comes after the domain.
 *
 * For example, in "www.example.com/about/en-us", the slug is "about/en-us"
 *
 * Leading and trailing slashes will be ignored, so "/about/" will be treated the same as "about"
 */
class Slug(value: String) {
    val value = value.trimStart { it == '/' }.trimEnd { it == '/' }
    val parts get() = this.value.split('/')
}