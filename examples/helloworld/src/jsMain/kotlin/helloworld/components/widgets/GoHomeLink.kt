package helloworld.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.navigation.Link

@Composable
fun GoHomeLink() = Link("/", "Go Home")