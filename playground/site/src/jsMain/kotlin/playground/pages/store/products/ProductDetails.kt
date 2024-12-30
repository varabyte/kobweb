package playground.pages.store.products

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import org.jetbrains.compose.web.dom.Text

@Composable
@Page("{...product-details}")
fun ProductDetails() {
    val ctx = rememberPageContext()
    Text("(Pretend we're showing product details for: ${ctx.route.params.getValue("product-details")})")
}