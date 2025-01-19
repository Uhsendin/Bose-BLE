package com.example.bose_ble.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.ConcurrentHashMap

private const val GATT_MAX_MTU_SIZE = 517
private const val GATT_MIN_MTU_SIZE = 23

@SuppressLint("MissingPermission")
object ConnectionManager {
    private val deviceGattMap = ConcurrentHashMap<BluetoothDevice, BluetoothGatt>()

    fun connect(device: BluetoothDevice, context: Context) {
                device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to $deviceAddress")
                    deviceGattMap[gatt.device] = gatt
                    Handler(Looper.getMainLooper()).post {
                        gatt.discoverServices()
                    }

                } else if (newState ==
                    BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfuly disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w("BluetoothGattCallback", "Error $status encoutnered for $deviceAddress! Disconnecting...")
                gatt.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.w("BluetoothGattCallback", "Discovered ${services.size} services for ${device.address}")
                    printGattTable()
                }
            }

        }
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.w("BluetoothGattCallback", "ATT MTU chagned to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")
        }

        @Deprecated("Deprecated for Android 13+")
        @Suppress("DEPRECATION")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with (characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i("BluetoothGattCallback", "Read characteristic $uuid:\n${value.toHexString()}")
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Log.e("BluetoothGattCallback", "Read not permitted for $uuid!")
                    }
                    else -> {
                        Log.e("BluetoothGattCalback", "Characteristic read failed for $uuid, error: $status")
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            val uuid = characteristic.uuid
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.i("BluetoothGattCallback", "Read characteristic $uuid:\n${value.toHexString()}")
                }
                BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                    Log.e("BluetoothGattCallback", "Read not permitted for $uuid!")
                }
                else -> {
                    Log.e("BluetoothGattCalback", "Characteristic read failed for $uuid, error: $status")
                }
            }
        }
    }
    fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") {
            String.format("%02x", it)
        }
}