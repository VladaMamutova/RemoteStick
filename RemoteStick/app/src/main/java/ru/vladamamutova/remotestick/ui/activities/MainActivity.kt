package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.ui.adapters.DeviceAdapter
import ru.vladamamutova.remotestick.utils.NetworkUtils
import ru.vladamamutova.remotestick.utils.doAsync
import java.net.SocketException


class MainActivity : AppCompatActivity() {

    private val deviceAdapter = DeviceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // use Kotlin Android Extensions
        device_list.layoutManager = LinearLayoutManager(this)
        device_list.adapter = deviceAdapter
    }

    fun updateDevices(view: View) {
        try {
            doAsync {
                NetworkUtils.discoverLocalNetwork(this)
                device_list.post {
                    deviceAdapter.setData(NetworkUtils.localDevices())
                }
            }

          /*  val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = manager.connectionInfo
            val ipAddress = connectionInfo.ipAddress
            val ip = InetAddress.getByAddress(ByteBuffer
                .allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(ipAddress)
                .array())
                .hostAddress*/

        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    fun addDevice(view: View) {
        val intent = Intent(this, AddDeviceActivity::class.java)
        startActivity(intent)
    }
}