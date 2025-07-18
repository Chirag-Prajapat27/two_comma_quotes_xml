package com.app.twocommaquotes.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.Serializable

@SuppressWarnings("WeakerAccess", "unused")
class PermissionCheck {
    companion object {
        var loggingEnabled = true

        /**
         * Disable logs.
         */
        fun disableLogging() {
            loggingEnabled = false
        }

        fun log(message: String) {
            if (loggingEnabled) Log.e("Permissions", message)
        }

        /**
         * Check/Request a permission and call the callback methods of permission handler accordingly.
         *
         * @param context    the android context.
         * @param permission the permission to be requested.
         * @param rationale  Explanation to be shown to user if s/he has denied permission earlier.
         * If this parameter is null, permissions will be requested without showing
         * the rationale dialog.
         * @param handler    The permission handler object for handling callbacks of various user
         * actions such as permission granted, permission denied, etc.
         */
        fun check(
            context: Context, permission: String, rationale: String,
            handler: PermissionHandler
        ) {
            val perm: ArrayList<String> = ArrayList()
            perm.add(permission)
            check(context, perm, rationale, null, handler)
        }

        fun check(
            context: Context, permission: ArrayList<String>, rationale: String,
            handler: PermissionHandler
        ) {

            check(context, permission, rationale, null, handler)
        }

        /**
         * Check/Request a permission and call the callback methods of permission handler accordingly.
         *
         * @param context     the android context.
         * @param permission  the permission to be requested.
         * @param rationaleId The string resource id of the explanation to be shown to user if s/he has
         * denied permission earlier. If resource is not found, permissions will be
         * requested without showing the rationale dialog.
         * @param handler     The permission handler object for handling callbacks of various user
         * actions such as permission granted, permission denied, etc.
         */
        fun check(
            context: Context, permission: String, rationaleId: Int,
            handler: PermissionHandler
        ) {
            var rationale: String? = null
            try {
                rationale = context.getString(rationaleId)
            } catch (ignored: Exception) {
            }
            val perm: ArrayList<String> = ArrayList()
            perm.add(permission)
            check(context, perm, rationale, null, handler)
        }

        /**
         * Check/Request permissions and call the callback methods of permission handler accordingly.
         *
         * @param context     Android context.
         * @param permissions The array of one or more permission(s) to request.
         * @param rationale   Explanation to be shown to user if s/he has denied permission earlier.
         * If this parameter is null, permissions will be requested without showing
         * the rationale dialog.
         * @param options     The options for handling permissions.
         * @param handler     The permission handler object for handling callbacks of various user
         * actions such as permission granted, permission denied, etc.
         */
        fun check(
            context: Context, permissions: ArrayList<String>, rationale: String?,
            options: Options?, handler: PermissionHandler
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                handler.onGranted()
                log("Android version < 23")
            } else {
                val permissionsSet = LinkedHashSet<String>()
                for (perm in permissions) {
                    permissionsSet.add(perm)
                }
                var allPermissionProvided = true
                for (aPermission in permissionsSet) {
                    if (context.checkSelfPermission(aPermission) != PackageManager.PERMISSION_GRANTED) {
                        allPermissionProvided = false
                        break
                    }
                }

                if (allPermissionProvided) {
                    handler.onGranted()
                    log(
                        "Permission(s) " + if (PermissionsActivity.permissionHandler == null)
                            "already granted."
                        else
                            "just granted from settings."
                    )
                    PermissionsActivity.permissionHandler = null

                } else {
                    PermissionsActivity.permissionHandler = handler
                    val permissionsList = ArrayList(permissionsSet)

                    val intent = Intent(context, PermissionsActivity::class.java)
                        .putExtra(PermissionsActivity.EXTRA_PERMISSIONS, permissionsList)
                        .putExtra(PermissionsActivity.EXTRA_RATIONALE, rationale)
                        .putExtra(PermissionsActivity.EXTRA_OPTIONS, options)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            }
        }

        /**
         * Check/Request permissions and call the callback methods of permission handler accordingly.
         *
         * @param context     Android context.
         * @param permissions The array of one or more permission(s) to request.
         * @param rationaleId The string resource id of the explanation to be shown to user if s/he has
         * denied permission earlier. If resource is not found, permissions will be
         * requested without showing the rationale dialog.
         * @param options     The options for handling permissions.
         * @param handler     The permission handler object for handling callbacks of various user
         * actions such as permission granted, permission denied, etc.
         */
        fun check(
            context: Context, permissions: ArrayList<String>, rationaleId: Int,
            options: Options, handler: PermissionHandler
        ) {
            var rationale: String? = null
            try {
                rationale = context.getString(rationaleId)
            } catch (ignored: Exception) {
            }
            check(context, permissions, rationale!!, options, handler)

        }
    }

    /**
     * Options to customize while requesting permissions.
     */
    class Options : Serializable {
        internal var settingsText = "Settings"
        internal var rationaleDialogTitle = "Permissions Required"
        internal var settingsDialogTitle = "Permissions Required"
        internal var settingsDialogMessage =
            "Required permission(s) have been set" + " not to ask again! Please provide them from settings."
        internal var sendBlockedToSettings = true

        /**
         * Sets the button text for "settings" while asking user to go to settings.
         *
         * @param settingsText The text for "settings".
         * @return same instance.
         */
        fun setSettingsText(settingsText: String): Options {
            this.settingsText = settingsText
            return this
        }

        /**
         * Sets the title text for permission rationale dialog.
         *
         * @param rationaleDialogTitle the title text.
         * @return same instance.
         */
        fun setRationaleDialogTitle(rationaleDialogTitle: String): Options {
            this.rationaleDialogTitle = rationaleDialogTitle
            return this
        }

        /**
         * Sets the title text of the dialog which asks user to go to settings, in the case when
         * permission(s) have been set not to ask again.
         *
         * @param settingsDialogTitle the title text.
         * @return same instance.
         */
        fun setSettingsDialogTitle(settingsDialogTitle: String): Options {
            this.settingsDialogTitle = settingsDialogTitle
            return this
        }

        /**
         * Sets the message of the dialog which asks user to go to settings, in the case when
         * permission(s) have been set not to ask again.
         *
         * @param settingsDialogMessage the dialog message.
         * @return same instance.
         */
        fun setSettingsDialogMessage(settingsDialogMessage: String): Options {
            this.settingsDialogMessage = settingsDialogMessage
            return this
        }

        /**
         * In the case the user has previously set some permissions not to ask again, if this flag
         * is true the user will be prompted to go to settings and provide the permissions otherwise
         * the method [PermissionHandler.onDenied] will be invoked
         * directly. The default state is true.
         *
         * @param send whether to ask user to go to settings or not.
         * @return same instance.
         */
        fun sendDoNotAskAgainToSettings(send: Boolean): Options {
            sendBlockedToSettings = send
            return this
        }
    }


}
