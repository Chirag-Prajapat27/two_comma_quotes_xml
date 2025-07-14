package com.app.twocommaquotes.permission

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.WindowManager

@SuppressWarnings("unchecked")
@TargetApi(Build.VERSION_CODES.M)
class PermissionsActivity : Activity() {

    companion object {
        private const val RC_SETTINGS = 6739
        private const val RC_PERMISSION = 6937

        internal const val EXTRA_PERMISSIONS = "permissions"
        internal const val EXTRA_RATIONALE = "rationale"
        internal const val EXTRA_OPTIONS = "options"

        internal var permissionHandler: PermissionHandler? = null
    }

    private var allPermissions: ArrayList<String>? = null
    private var deniedPermissions: ArrayList<String>? = null
    private var noRationaleList: ArrayList<String>? = null
    private var options: PermissionCheck.Options? = null

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setFinishOnTouchOutside(false)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val intent = intent
        if (intent == null || !intent.hasExtra(EXTRA_PERMISSIONS)) {
            finish()
            return
        }

        //window.statusBarColor = 0
        allPermissions = intent.getSerializableExtra(EXTRA_PERMISSIONS) as ArrayList<String>?
        options = if (intent.getSerializableExtra(EXTRA_OPTIONS) == null) {
            PermissionCheck.Options()
        } else {
            intent.getSerializableExtra(EXTRA_OPTIONS) as PermissionCheck.Options
        }
        deniedPermissions = ArrayList()
        noRationaleList = ArrayList()

        var noRationale = true
        for (permission in allPermissions!!) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions!!.add(permission)
                if (shouldShowRequestPermissionRationale(permission)) {
                    noRationale = false
                } else {
                    noRationaleList!!.add(permission)
                }
            }
        }

        if (deniedPermissions!!.isEmpty()) {
            grant()
            return
        }

        val rationale = intent.getStringExtra(EXTRA_RATIONALE)
        if (noRationale || TextUtils.isEmpty(rationale)) {
            PermissionCheck.log("No rationale.")
            requestPermissions(toArray(deniedPermissions!!), RC_PERMISSION)
        } else {
            PermissionCheck.log("Show rationale.")
            showRationale(rationale!!)
        }
    }

    private fun showRationale(rationale: String) {
        val listener = DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                requestPermissions(toArray(deniedPermissions!!), RC_PERMISSION)
            } else {
                deny()
            }
        }
        AlertDialog.Builder(this).setTitle(options!!.rationaleDialogTitle)
            .setMessage(rationale)
            .setPositiveButton(android.R.string.ok, listener)
            .setNegativeButton(android.R.string.cancel, listener)
            .setOnCancelListener { deny() }.create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty()) {
            deny()
        } else {
            deniedPermissions!!.clear()
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions!!.add(permissions[i])
                }
            }
            if (deniedPermissions!!.size == 0) {
                PermissionCheck.log("Just allowed.")
                grant()
            } else {
                val blockedList = ArrayList<String>() //set not to ask again.
                val justBlockedList = ArrayList<String>() //just set not to ask again.
                val justDeniedList = ArrayList<String>()
                for (permission in deniedPermissions!!) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        justDeniedList.add(permission)
                    } else {
                        blockedList.add(permission)
                        if (!noRationaleList!!.contains(permission)) {
                            justBlockedList.add(permission)
                        }
                    }
                }

                if (justBlockedList.size > 0) { //checked don't ask again for at least one.
                    val permissionHandler = permissionHandler
                    finish()
                    permissionHandler?.onJustBlocked(
                        applicationContext, justBlockedList,
                        deniedPermissions!!
                    )

                } else if (justDeniedList.size > 0) { //clicked deny for at least one.
                    deny()

                } else { //unavailable permissions were already set not to ask again.
                    if (permissionHandler != null && !permissionHandler!!.onBlocked(
                            applicationContext,
                            blockedList
                        )
                    ) {
                        sendToSettings()

                    } else
                        finish()
                }
            }
        }
    }

    private fun sendToSettings() {
        if (!options!!.sendBlockedToSettings) {
            deny()
            return
        }
        PermissionCheck.log("Ask to go to settings.")
        AlertDialog.Builder(this).setTitle(options!!.settingsDialogTitle)
            .setMessage(options!!.settingsDialogMessage)
            .setPositiveButton(options!!.settingsText) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                startActivityForResult(intent, RC_SETTINGS)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> deny() }
            .setOnCancelListener { deny() }.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SETTINGS && permissionHandler != null) {
            PermissionCheck.check(
                this, allPermissions!!, null, this.options!!,
                permissionHandler!!
            )
        }
        // super, because overridden method will make the handler null, and we don't want that.
        super.finish()
    }

    private fun toArray(arrayList: ArrayList<String>): Array<String?> {
        val size = arrayList.size
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) {
            array[i] = arrayList[i]
        }
        return array
    }

    override fun finish() {
        permissionHandler = null
        super.finish()
    }

    private fun deny() {
        val permissionHandler = permissionHandler
        finish()
        permissionHandler?.onDenied(applicationContext, deniedPermissions!!)
    }

    private fun grant() {
        val permissionHandler = permissionHandler
        finish()
        permissionHandler?.onGranted()
    }
}
