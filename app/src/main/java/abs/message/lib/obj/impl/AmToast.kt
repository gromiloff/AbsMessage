package abs.message.lib.obj.impl

import abs.message.lib.impl.WorkMessageWithContext
import abs.message.lib.obj.TypedMessage
import android.app.Activity
import android.view.Gravity
import android.widget.Toast

@Suppress("unused")
open class AmToast(message: Any) : TypedMessage<Any>(message), WorkMessageWithContext {
    override fun work(activity: Activity?) =
            if (activity == null) {
                false
            } else {
                activity.runOnUiThread({
                    val message = getObject()
                    val t = Toast.makeText(activity,
                            if (message is Int) {
                                activity.resources.getString(message)
                            } else {
                                message as String
                            },
                            Toast.LENGTH_LONG)
                    t.setGravity(Gravity.CENTER, 0, 0)
                    t.show()
                })
                true
            }
}