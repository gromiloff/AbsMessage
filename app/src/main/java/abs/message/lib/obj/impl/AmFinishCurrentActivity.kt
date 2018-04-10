package abs.message.lib.obj.impl

import abs.message.lib.impl.WorkMessageWithContext
import abs.message.lib.obj.IAbsoluteMessage
import android.app.Activity

@Suppress("unused")
class AmFinishCurrentActivity(result: Int = Activity.RESULT_OK) : IAbsoluteMessage(result), WorkMessageWithContext {
    override fun work(activity: Activity?): Boolean {
        return if (activity == null) {
            false
        } else {
            activity.setResult(getItem(0)!!)
            activity.finish()
            true
        }
    }
}