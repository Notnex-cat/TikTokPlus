package com.example.tiktokplus

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null
    
    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startService(Intent(this@MainActivity, FloatingWindowGFG::class.java))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!checkOverlayDisplayPermission()) {
            requestOverlayDisplayPermission()
            return
        }

        startService(Intent(this@MainActivity, FloatingWindowGFG::class.java))

        val launchIntent = packageManager.getLaunchIntentForPackage("com.zhiliaoapp.musically")
        if (launchIntent != null) {
            startActivity(launchIntent)
        }

        if (isServiceRunning(FloatingWindowGFG::class.java)) {
            stopService(Intent(this@MainActivity, FloatingWindowGFG::class.java))
        }
        startService(Intent(this@MainActivity, FloatingWindowGFG::class.java))
        finish()
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        
        val intent = Intent(this, serviceClass)
        val pendingIntent = PendingIntent.getService(this, 0, intent, flags)
        
        return pendingIntent != null
    }

    private fun requestOverlayDisplayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Для Android < 6.0 разрешение не требуется
            startService(Intent(this@MainActivity, FloatingWindowGFG::class.java))
            finish()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission Needed")
        builder.setMessage("Enable 'Display over other apps' from System Settings.")
        builder.setPositiveButton("Open Settings") { _, _ ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        }
        dialog = builder.create()
        dialog!!.show()
    }

    private fun checkOverlayDisplayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true // Для Android < 6.0 разрешение не требуется
        }
    }
}
