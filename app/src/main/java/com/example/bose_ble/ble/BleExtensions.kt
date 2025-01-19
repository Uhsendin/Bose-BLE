package com.example.bose_ble.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
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

// BluetootGatt Characteristic

fun BluetoothGattCharacteristic.isReadable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

fun BluetoothGattCharacteristic.isWritable(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
    containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
    return properties and property != 0
}
