package abs.message.lib

import abs.message.lib.impl.ConsumeMessageListener
import abs.message.lib.obj.IAbsoluteMessage
import android.app.Application

open class AbsoluteMessageApplication : Application(), ConsumeMessageListener {

    override fun onCreate() {
        super.onCreate()
        AbsoluteMessageController.init(this)
    }

    override fun keyCheck() = IAbsoluteMessage.defaultKeyCheckValue

    override fun consume(intent: IAbsoluteMessage) = false
}
