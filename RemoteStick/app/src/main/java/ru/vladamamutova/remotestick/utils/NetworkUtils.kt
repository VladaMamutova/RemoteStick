package ru.vladamamutova.remotestick.utils

import android.content.Context
import android.net.wifi.WifiManager
import ru.vladamamutova.remotestick.model.Device
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder


class NetworkUtils {

    companion object {
        private val localDevices = ArrayList<Device>()

        fun localDevices() = localDevices

        fun discoverLocalNetwork(context: Context) {
            localDevices.clear()
            try {
                val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val connectionInfo = manager.connectionInfo
                val ipAddress = connectionInfo.ipAddress
                val ipString = InetAddress.getByAddress(
                    ByteBuffer
                        .allocate(4)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(ipAddress)
                        .array()
                )
                    .hostAddress

                val prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)

                for (i in 1..254) {
                    val testIp = prefix + i.toString()
                    val address: InetAddress = InetAddress.getByName(testIp)
                    if (address.isReachable(100)) {
                        if (address.hostAddress != ipString) {
                            localDevices.add(Device(address.canonicalHostName, testIp))
                        }
                    }
                }
            } catch (t: Throwable) {
            }
        }
    }
}

