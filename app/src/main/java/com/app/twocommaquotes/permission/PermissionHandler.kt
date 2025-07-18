package com.app.twocommaquotes.permission

import android.content.Context
import android.widget.Toast
import java.security.Permissions
import java.util.*

@SuppressWarnings("WeakerAccess")
abstract class PermissionHandler {
    /**
     * This method will be called if all of the requested permissions are granted.
     */
    abstract fun onGranted()

    /**
     * This method will be called if some of the requested permissions have been denied.
     *
     * @param context           The application context.
     * @param deniedPermissions The list of permissions which have been denied.
     */
    abstract fun onDenied(context: Context, deniedPermissions: ArrayList<String>)

    fun onPermissionDenied(context: Context, deniedPermissions: ArrayList<String>) {
        if (PermissionCheck.loggingEnabled) {
            val builder = StringBuilder()
            builder.append("Denied:")
            for (permission in deniedPermissions) {
                builder.append(" ")
                builder.append(permission)
            }
            PermissionCheck.log(builder.toString())
        }
        Toast.makeText(context, "Permission Denied.", Toast.LENGTH_SHORT).show()
    }


    /**
     * This method will be called if some permissions have previously been set not to ask again.
     *
     * @param context     the application context.
     * @param blockedList the list of permissions which have been set not to ask again.
     * @return The overrider of this method should return true if no further action is needed,
     * and should return false if the default action is to be taken, i.e. send user to settings.
     * <br></br><br></br>
     * Note: If the option [Permissions.Options.sendDontAskAgainToSettings] has been
     * set to false, the user won't be sent to settings by default.
     */
    fun onBlocked(context: Context, blockedList: ArrayList<String>): Boolean {
        if (PermissionCheck.loggingEnabled) {
            val builder = StringBuilder()
            builder.append("Set not to ask again:")
            for (permission in blockedList) {
                builder.append(" ")
                builder.append(permission)
            }
            PermissionCheck.log(builder.toString())
        }
        return false
    }

    /**
     * This method will be called if some permissions have just been set not to ask again.
     *
     * @param context           The application context.
     * @param justBlockedList   The list of permissions which have just been set not to ask again.
     * @param deniedPermissions The list of currently unavailable permissions.
     */
    fun onJustBlocked(
        context: Context, justBlockedList: ArrayList<String>,
        deniedPermissions: ArrayList<String>
    ) {
        if (PermissionCheck.loggingEnabled) {
            val builder = StringBuilder()
            builder.append("Just set not to ask again:")
            for (permission in justBlockedList) {
                builder.append(" ")
                builder.append(permission)
            }
            PermissionCheck.log(builder.toString())
        }
        onDenied(context, deniedPermissions)
    }
}