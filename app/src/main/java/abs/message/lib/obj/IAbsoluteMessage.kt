package abs.message.lib.obj

import abs.message.lib.AbsoluteMessageController
import android.os.Bundle
import android.util.Log

abstract class IAbsoluteMessage {
    companion object {
        const val keyBundle = "IAbsoluteMessage.keyBundle"
        const val keyCommand = "IAbsoluteMessage.keyCommand"
        // const val keyCount = "IAbsoluteMessage.keyCount"
        const val keyValue = "IAbsoluteMessage.keyValue"
        //const val keyData = "IAbsoluteMessage.keyData"
        const val keyTimeStump = "IAbsoluteMessage.keyTimeStump"
        const val keyCheck = "IAbsoluteMessage.keyCheck"

        const val defaultKeyCheckValue = 0L
    }

    object Creator {
        fun classToBundle(src: IAbsoluteMessage): Bundle {
            val b = Bundle()
            b.putSerializable(keyValue, src.items)

            if (AbsoluteMessageController.enableLog) {
                Log.v(src::class.java.simpleName, ">>> BINDED with ${src.items}")
            }
            return b
        }

        fun bundleToClass(className: String, b: Bundle): IAbsoluteMessage {
            val classData = Class.forName(className).getConstructor().newInstance() as IAbsoluteMessage
            @Suppress("UNCHECKED_CAST")
            classData.setMap(b.getSerializable(keyValue) as HashMap<Int, Any?>)

            if (AbsoluteMessageController.enableLog) {
                Log.v(classData::class.java.simpleName, "<<< UNBINDED with ${classData.items}")
            }
            return classData
        }
    }

    constructor() {
    }

    constructor(vararg preloadData: Any?) : this() {
        for ((idx, preloadItem) in preloadData.withIndex()) {
            this.items[idx] = preloadItem
        }
    }

    /*
     * данные в классе хранятся в HashMap структуре данных, где ключем является индекс (Int).
     * Может храниться в качествен данных и null
     */
    private val items: HashMap<Int, Any?> = HashMap()

    open fun keyCheck() = defaultKeyCheckValue

    @Suppress("UNCHECKED_CAST")
    open fun <T> getItem(index: Int) = this.items[index] as? T

    open fun addItem(index: Int, data: Any?) {
        this.items[index] = data
    }

    fun addItem(data: Any?) {
        this.items[this.items.size] = data
    }

    fun size() = this.items.size

    fun send() {
        AbsoluteMessageController.post(this)
    }

    private fun setMap(data: HashMap<Int, Any?>) {
        this.items.clear()
        data.forEach { item -> this.items[item.key] = item.value }
    }
}