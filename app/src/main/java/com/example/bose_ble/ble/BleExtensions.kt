package com.example.bose_ble.ble

import android.bluetooth.BluetoothGatt
import android.util.Log

// BluetoothGatt

fun BluetoothGatt.printGattTable() {
    if (services.isEmpty()) {
        Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?")
    }
    services.forEach {service ->
        val characteristicTable =
            service.characteristics.joinToString(
                separator = "\n!--",
                prefix = "!--"
            ) {it.uuid.toString()}
        Log.i("printGattTable", "\nService ${service.uuid}\nCharacteristics:\n$characteristicTable")
    }
}
