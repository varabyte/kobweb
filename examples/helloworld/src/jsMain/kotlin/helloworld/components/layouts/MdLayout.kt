package helloworld.components.layouts

import androidx.compose.runtime.Composable

@Composable
fun MdLayout(data: Map<String, List<String>>, content: @Composable () -> Unit) {
    val title = data.getValue("title").single()
    PageLayout(title, content)
}