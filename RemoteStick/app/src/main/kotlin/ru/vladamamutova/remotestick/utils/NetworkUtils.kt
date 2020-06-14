package ru.vladamamutova.remotestick.utils

import android.content.Context
import android.net.wifi.WifiManager
import ru.vladamamutova.remotestick.model.Device
import ru.vladamamutova.remotestick.service.RemoteStickClient
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder


class NetworkUtils {

    companion object {
        private val localDevices = ArrayList<Device>()

        fun localDevices() = localDevices

        private fun getLocalIpAddress(context: Context) : InetAddress {
            val manager = context.applicationContext.getSystemService(
                Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = manager.connectionInfo
            val ipAddress = connectionInfo.ipAddress
            return InetAddress.getByAddress(
                ByteBuffer
                    .allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(ipAddress)
                    .array()
            )
        }

        fun discoverLocalNetwork(context: Context) {
            localDevices.clear()
            try {
                val ipAddress = getLocalIpAddress(context).hostAddress
                val prefix = ipAddress.substring(0, ipAddress.lastIndexOf(".") + 1)

                for (i in 1..254) {
                    val testIp = prefix + i.toString()
                    val address: InetAddress = InetAddress.getByName(testIp)
                    if (address.isReachable(100)) {
                        if (address.hostAddress != ipAddress) {
                            try {
                                val serverName = RemoteStickClient.pingServer(address)
                                localDevices.add(Device(serverName, testIp))
                            } catch (ex: Exception) { }
                        }
                    }
                }
            } catch (t: Throwable) {
            }
        }
    }
}

