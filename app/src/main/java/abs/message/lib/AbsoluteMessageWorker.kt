package abs.message.lib

import abs.message.lib.impl.ConsumeMessageListener
import abs.message.lib.impl.WorkMessage
import abs.message.lib.impl.WorkMessageWithContext
import abs.message.lib.obj.ApplicationMessage
import abs.message.lib.obj.IAbsoluteMessage
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import java.util.*

/*
* Изменить конструкцию, теперь параметры из анбинда автоматически должны создавать класс
* */
internal class AbsoluteMessageWorker : AsyncTask<Intent?, ConsumeMessageListener, Void?>() {
    private var absMessageImpl: IAbsoluteMessage? = null
    private var consumed = false

    override fun doInBackground(vararg params: Intent?): Void? {
        if (params.isEmpty()) {
            Log.e("AbsoluteMessageWorker", "doInBackground income empty data params")
            return null
        }
        val intent = params[0]!!
        val name = intent.getStringExtra(IAbsoluteMessage.keyCommand)
        val keyCheck = intent.getLongExtra(IAbsoluteMessage.keyCheck, 0L)
        val tmp = name.split(".")
        Thread.currentThread().name = tmp[tmp.size - 1]

        this.absMessageImpl = IAbsoluteMessage.Creator.bundleToClass(name, intent.getBundleExtra(IAbsoluteMessage.keyBundle))

        if (this.absMessageImpl != null) {
            val specialCase = when (this.absMessageImpl) {
                is WorkMessageWithContext -> (this.absMessageImpl!! as WorkMessageWithContext).work(AbsoluteMessageController.getCurrentActivity())
                is WorkMessage -> (this.absMessageImpl!! as WorkMessage).work()
                is ApplicationMessage<*> -> {
                    publishProgress(AbsoluteMessageController.application)
                    true
                }
                else -> false
            }

            // если событие не было специально обработано - отдаем всем слушателям в приложение
            if (!specialCase) {

                val stored = HashSet(AbsoluteMessageController.customListeners)
                // пытаемся отправить всему стуку слушателей, исключая активити и апликейшен
                for (callback in stored) {
                    if (keyCheck == IAbsoluteMessage.defaultKeyCheckValue || callback.keyCheck() == keyCheck) {
                        publishProgress(callback)
                        if (this.consumed) {
                            break
                        }
                    }
                }
                if (!this.consumed) {
                    // если событие не было скушано ранее, пытаемся отправить в активити, потом в апликейшен
                    if (keyCheck == IAbsoluteMessage.defaultKeyCheckValue) {
                        publishProgress(AbsoluteMessageController.getCurrentActivityListener(), AbsoluteMessageController.application)
                    } else {
                        if (keyCheck == AbsoluteMessageController.getCurrentActivityListener()?.keyCheck()) {
                            publishProgress(AbsoluteMessageController.getCurrentActivityListener())
                        }
                        if (keyCheck == AbsoluteMessageController.application.keyCheck()) {
                            publishProgress(AbsoluteMessageController.application)
                        }
                    }
                }
                // патыемся отправить всем слушателям, кто не биндится к активити
                if (AbsoluteMessageController.noBindingListeners.isNotEmpty()) {
                    publishProgress(*AbsoluteMessageController.noBindingListeners.toTypedArray())
                }
            }
        }
        return null
    }

    override fun onProgressUpdate(vararg values: ConsumeMessageListener?) {
        var can = true
        for (cb in values) {
            if (cb != null && !this.consumed) {
                when (cb) {
                    is Activity -> {
                        if (cb.isFinishing || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && cb.isDestroyed)) {
                            can = false
                            Log.d(this.absMessageImpl!!::class.java.simpleName, "object cannot be consumed by ${cb::class.java.simpleName} because activity is dead")
                        }
                    }
                    is android.support.v4.app.Fragment -> {
                        if (cb.activity == null) {
                            can = false
                            Log.d(this.absMessageImpl!!::class.java.simpleName, "object cannot be consumed by ${cb::class.java.simpleName} because fragment is not attached to activity")
                        }
                    }
                }
                if (can) {
                    this.consumed = cb.consume(this.absMessageImpl!!)
                    if (AbsoluteMessageController.enableLog) {
                        Log.d(this.absMessageImpl!!::class.java.simpleName, "try consume by ${cb::class.java.simpleName} return ${this.consumed}")
                    }
                }
                can = true
            }
            if (this.consumed) {
                break
            }
        }
    }
}