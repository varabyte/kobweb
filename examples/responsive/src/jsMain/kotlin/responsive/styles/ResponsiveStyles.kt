package responsive.styles

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.*

val MobileHiddenStyle = ComponentStyle("mobile-hidden") {
    base {
        Modifier.display(DisplayStyle.None)
    }

    Breakpoint.MD {
        Modifier.display(DisplayStyle.Inherit)
    }
}

val DesktopHiddenStyle = ComponentStyle("desktop-hidden") {
    Breakpoint.MD {
        Modifier.display(DisplayStyle.None)
    }
}
