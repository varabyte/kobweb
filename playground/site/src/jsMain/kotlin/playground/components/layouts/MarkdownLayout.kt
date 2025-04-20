package playground.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobwebx.markdown.markdown

@InitRoute
fun initMarkdownLayout(ctx: InitRouteContext) {
    val fm = ctx.markdown!!.frontMatter
    ctx.data.add(PageLayoutData(fm["title"]?.singleOrNull() ?: "(Markdown missing title)"))
}

@Composable
@Layout(".components.layouts.PageLayout")
fun MarkdownLayout(content: @Composable () -> Unit) {
    content()
}
