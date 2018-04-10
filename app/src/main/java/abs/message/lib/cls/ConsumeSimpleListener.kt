package abs.message.lib.cls

import abs.message.lib.impl.ConsumeMessageListener

abstract class ConsumeSimpleListener : ConsumeMessageListener {
    override fun keyCheck() = 0L
}