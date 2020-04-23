package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_add_device.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.RemoteControlManager
import ru.vladamamutova.remotestick.utils.InputUtils
import java.lang.Exception
import java.net.InetAddress


class AddDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        edit_ip_address.filters = InputUtils.getIpAddressFilters()
        edit_ip_address.doAfterTextChanged {
            val splits = it.toString().split("\\.".toRegex())
                .filter { split -> !split.isBlank() }
                connect_button.isEnabled = splits.size == 4
            }
    }

    fun connect(view: View) {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val response = RemoteControlManager.pingServer(
            InetAddress.getByName(edit_ip_address.text.toString())
        )

        if (response.isNotEmpty()) {
            try {
                RemoteControlManager.myInstance.connect(this,
                    InetAddress.getByName(edit_ip_address.text.toString()))

                Toast.makeText(this, "Подключено к $response", Toast.LENGTH_SHORT).show()

                RemoteControlManager.myInstance.run()

                val intent = Intent(this, ControlActivity::class.java)
                startActivity(intent)
            }
            catch (ex : Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
