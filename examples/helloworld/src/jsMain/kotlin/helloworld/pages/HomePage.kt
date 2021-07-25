package helloworld.pages

import TestComposable
import androidx.compose.runtime.Composable
import nekt.core.Page

class HomePage : Page(isIndex = true) {
    @Composable
    override fun render() {
        TestComposable(slug.value)
    }
}