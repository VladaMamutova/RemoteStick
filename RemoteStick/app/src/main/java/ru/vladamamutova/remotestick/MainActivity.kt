package ru.vladamamutova.remotestick

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.vladamamutova.remotestick.model.Device
import ru.vladamamutova.remotestick.ui.DeviceAdapter
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val deviceAdapter = DeviceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // use Kotlin Android Extensions
        device_list.layoutManager = LinearLayoutManager(this)
        device_list.adapter = deviceAdapter

        // Позволяем выполнять сетевые операции в главном потоке.
        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun updateDevices(view: View) {
        try {
            val devices = ArrayList<Device>()

            val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = manager.connectionInfo
            val ipAddress = connectionInfo.ipAddress
            val ip = InetAddress.getByAddress(ByteBuffer
                    .allocate(4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putInt(ipAddress)
                    .array())
                .hostAddress

            devices.add(Device("my ip", ip))
            val interfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val nInterface: NetworkInterface = interfaces.nextElement()
                //if (nInterface.isUp) {
                    val addresses: Enumeration<InetAddress> = nInterface.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val address: InetAddress = addresses.nextElement()
                        if (address.isSiteLocalAddress && !address.isLoopbackAddress) {
                            devices.add(Device(address.hostName, address.hostAddress))
                        }
                    }
                //}
            }
            deviceAdapter.setData(devices)
        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    fun addDevice(view: View) {
        val intent = Intent(this, AddDeviceActivity::class.java)
        startActivity(intent)
    }
}
