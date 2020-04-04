package ru.vladamamutova.remotestick

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_add_device.*
import ru.vladamamutova.remotestick.utils.InputUtils
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
/*
        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
*/

        val manager = RemoteControlManager(InetAddress.getByName(edit_ip_address.text.toString()))
        manager.execute()
    }
}
