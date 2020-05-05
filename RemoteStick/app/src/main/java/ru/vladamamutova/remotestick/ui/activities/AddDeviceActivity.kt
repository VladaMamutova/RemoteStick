package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_add_device.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.RemoteControlManager
import ru.vladamamutova.remotestick.utils.InputUtils
import ru.vladamamutova.remotestick.utils.doAsync
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
        doAsync {
            try {
                val ip = InetAddress.getByName(edit_ip_address.text.toString())
                val response = RemoteControlManager.myInstance.connect(ip)
                runOnUiThread(Runnable {
                    Toast.makeText(
                        this, "Подключено к $response", Toast.LENGTH_SHORT
                    ).show()
                })

                val intent = Intent(this, ControlActivity::class.java)
                startActivity(intent)
                finish()
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this, ex.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
