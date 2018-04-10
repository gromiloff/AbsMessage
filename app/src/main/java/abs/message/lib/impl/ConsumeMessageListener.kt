package abs.message.lib.impl

import abs.message.lib.obj.IAbsoluteMessage


interface ConsumeMessageListener {
    /*
    * @return - true if message was consumed
    * */
    fun consume(intent: IAbsoluteMessage): Boolean

    /*
    * @return - nanotime of event class owner
    * */
    fun keyCheck(): Long
}