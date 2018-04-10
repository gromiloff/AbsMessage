package abs.message.lib.impl

import android.app.Activity

interface WorkMessageWithContext {
    fun work(activity: Activity?): Boolean
}