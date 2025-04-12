package playground.pages.store.products

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.PageContext
import org.jetbrains.compose.web.dom.Text

@Composable
@Page("{...}")
fun ProductDetails(ctx: PageContext) {
    Text("(Pretend we're showing product details for: ${ctx.route.params.getValue("product-details")})")
}