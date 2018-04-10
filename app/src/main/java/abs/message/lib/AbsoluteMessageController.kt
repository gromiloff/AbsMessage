package abs.message.lib

import abs.message.lib.impl.ConsumeMessageListener
import abs.message.lib.obj.IAbsoluteMessage
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.util.*

/**
 * Created by @gromiloff on 16.02.2018
 */

class AbsoluteMessageController : BroadcastReceiver(), Application.ActivityLifecycleCallbacks {
    companion object Instance {
        lateinit var application: AbsoluteMessageApplication
        @SuppressLint("StaticFieldLeak")
        private var currentActivity: ConsumeMessageListener? = null
        internal val customListeners = HashSet<ConsumeMessageListener>()
        internal val noBindingListeners = HashSet<ConsumeMessageListener>()

        var enableLog = false

        fun init(app: AbsoluteMessageApplication) {
            this.application = app
            val inst = AbsoluteMessageController()
            this.application.registerActivityLifecycleCallbacks(inst)
            LocalBroadcastManager.getInstance(this.application).registerReceiver(
                    inst,
                    IntentFilter(this.application.resources.getString(R.string.am_action)))
        }

        fun openActivity(activity: Activity?) {
            if (activity != this.currentActivity && activity is ConsumeMessageListener) {
                Log.e("Controller", "open new activity $activity")
                this.customListeners.clear()
                this.currentActivity = activity
            }
        }

        @Suppress("unused")
        fun addAdditionalListener(listener: ConsumeMessageListener) {
            Log.e("Controller", "addAdditionalListener $listener")
            this.customListeners.add(listener)
        }

        @Suppress("unused")
        fun removeAdditionalListener(listener: ConsumeMessageListener) {
            Log.e("Controller", "removeAdditionalListener $listener")
            this.customListeners.remove(listener)
        }

        @Suppress("unused")
        fun addNoBindingListener(listener: ConsumeMessageListener) {
            Log.e("Controller", "addNoBindingListener $listener")
            this.noBindingListeners.add(listener)
        }

        @Suppress("unused")
        fun removeNoBindingListener(listener: ConsumeMessageListener) {
            Log.e("Controller", "removeNoBindingListener $listener")
            this.noBindingListeners.remove(listener)
        }

        fun getCurrentActivity() = if (this.currentActivity != null) this.currentActivity as Activity else null

        fun getCurrentActivityListener() = if (this.currentActivity != null) this.currentActivity else null

        fun post(event: IAbsoluteMessage) {
            val intent = Intent(this.application.resources.getString(R.string.am_action))
            intent.putExtra(IAbsoluteMessage.keyBundle, IAbsoluteMessage.Creator.classToBundle(event))
            intent.putExtra(IAbsoluteMessage.keyTimeStump, System.nanoTime())
            intent.putExtra(IAbsoluteMessage.keyCommand, event::class.java.canonicalName)
            intent.putExtra(IAbsoluteMessage.keyCheck, event.keyCheck())
            LocalBroadcastManager.getInstance(this.application).sendBroadcast(intent)
        }
    }

    override fun onActivityPaused(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {
        // на случай когда предыдущая активити запускалась с флагами прозрачности - 
        // onStart при возврате не будет вызван
        openActivity(activity)
    }

    override fun onActivityStarted(activity: Activity?) {
        openActivity(activity)
    }

    override fun onActivityDestroyed(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // на случай когда предыдущая активити запускалась с флагами прозрачности - 
        // onStart при возврате не будет вызван
        openActivity(activity)
    }

    override fun onReceive(context: Context, i: Intent?) {
        AbsoluteMessageWorker().execute(i)
    }
}
