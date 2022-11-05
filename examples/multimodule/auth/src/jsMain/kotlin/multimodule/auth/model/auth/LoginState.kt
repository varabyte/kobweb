package multimodule.auth.model.auth

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.navigation.Router
import com.varabyte.kobweb.silk.components.icons.fa.FaRightFromBracket
import multimodule.core.components.sections.ExtraNavHeaderAction
import multimodule.core.components.sections.NavHeaderAction

sealed class LoginState {
    companion object {
        private val mutableLoginState by lazy { mutableStateOf<LoginState>(LoggedOut) }

        var current
            get() = mutableLoginState.value
            set(value) {
                mutableLoginState.value = value

                when (value) {
                    is LoggedIn -> {
                        ExtraNavHeaderAction.current = object : NavHeaderAction() {
                            @Composable
                            override fun BoxScope.renderAction() {
                                FaRightFromBracket()
                            }

                            override fun onActionClicked(router: Router) {
                                LoginState.current = LoggedOut
                                router.navigateTo("/")
                            }
                        }
                    }
                    LoggedOut -> {
                        ExtraNavHeaderAction.current = null
                    }
                }
            }
    }

    object LoggedOut : LoginState()
    class LoggedIn(val account: Account) : LoginState()
}