package com.thatwaz.weathercast.model.util

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtils {

    fun requestLocationPermissions(
        context: Context,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        onPermissionGranted.invoke()
                    } else {
                        onPermissionDenied.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermission(context, token)
                }
            })
            .check()
    }

    private fun showRationalDialogForPermission(
        context: Context,
        token: PermissionToken?
    ) {
        MaterialAlertDialogBuilder(context)
            .setMessage("It looks like you have turned off permissions")
            .setPositiveButton("Go To Settings") { _, _ ->
                openAppSettings(context)
                token?.continuePermissionRequest()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                token?.cancelPermissionRequest()
            }
            .setOnCancelListener {
                token?.cancelPermissionRequest()
            }
            .show()
    }

    private fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}