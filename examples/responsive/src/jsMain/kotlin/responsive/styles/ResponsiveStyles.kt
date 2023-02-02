package responsive.styles

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.*

val MobileHiddenStyle by ComponentStyle {
    base {
        Modifier.display(DisplayStyle.None)
    }

    Breakpoint.MD {
        Modifier.display(DisplayStyle.Inherit)
    }
}

val DesktopHiddenStyle by ComponentStyle {
    Breakpoint.MD {
        Modifier.display(DisplayStyle.None)
    }
}
