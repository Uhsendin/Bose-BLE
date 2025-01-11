package com.example.bose_ble

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.bose_ble.ui.theme.BoseBLETheme

private const val PERMISSION_REQUEST_CODE = 1

class MainActivity : ComponentActivity() {
    

    /****************************************
     * Private functions
     ***************************************/
    /****************************************
     * Prompts the user to enable Bluetooth via system dialog
     */
    private fun startBleScan() {
        if (!hasRequiredBluetoothPermissions()) {
            requestRelevantRuntimePermissions()
        } else {
            // Do something
        }
    }

    private fun Activity.requestRelevantRuntimePermissions() {
        if (hasRequiredBluetoothPermissions()) {return}
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                requestLocationsPermission()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                requestBluetoothPermissions()
            }
        }
    }

    private fun requestLocationsPermission() = runOnUiThread {
        AlertDialog.Builder(this)
            .setTitle("Location permission required")
            .setMessage("Starting from Android M (6.0), the system requires apps to be granted " +
                    "location access in order to scan for BLE devices.")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) {_, _ ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                    )
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermissions() = runOnUiThread {
        AlertDialog.Builder(this)
            .setTitle("Bluetooth permission required")
            .setMessage("Starting from Android M (6.0), the system requires apps to be granted" +
                    "location access in order to scan for BLE devices.")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) {_, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }
            .show()
    }
}