package com.example.bose_ble

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputBinding
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.bose_ble.ui.theme.BoseBLETheme
import timber.log.Timber

private const val PERMISSION_REQUEST_CODE = 1

class MainActivity : ComponentActivity() {

    private lateinit var binding: MainActivity


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
        if (hasRequiredBluetoothPermissions()) {
            return
        }
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
            .setMessage(
                "Starting from Android M (6.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
            )
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
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
            .setMessage(
                "Starting from Android M (6.0), the system requires apps to be granted" +
                        "location access in order to scan for BLE devices."
            )
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startScanButton: Button = findViewById(R.id.scan_button)

        startScanButton.setOnClickListener{
            startBleScan()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return
        }
        if (permissions.isEmpty() && grantResults.isEmpty()) {
            Timber.e("Empty permissions and grantResults array in onRequestPermissionsResult")
            Timber.w("This is likely a cancellation due to user interaction interrupted")
            return
        }

        // Log permission request outcomes
        val resultsDescriptions = grantResults.map {
            when (it) {
                PackageManager.PERMISSION_DENIED -> "Denied"
                PackageManager.PERMISSION_GRANTED -> "Granted"
                else -> "Unknown"
            }
        }
        Timber.w("Permissions: ${permissions.toList()}, grant results: $resultsDescriptions")

        // A denied permission is permanently denied if shouldShowRequestPermissionRationale is false
        val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
            it.second == PackageManager.PERMISSION_DENIED &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
        }
        val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        when {
            containsPermanentDenial -> {
                Timber.e("User permanently denied granting of permissions")
                Timber.e("Requesting for manual granting of permissions from App Settings")
            }

            containsDenial -> {
                // It's still possible to re-request permissions
                requestRelevantRuntimePermissions()
            }

            allGranted && hasRequiredBluetoothPermissions() -> {
                startBleScan()
            }

            else -> {
                Timber.e("Unexpected scenario encountered when handling permissions")
                recreate()
            }
        }
    }

}
