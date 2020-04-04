package ru.vladamamutova.remotestick

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_add_device.*
import ru.vladamamutova.remotestick.utils.InputUtils


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
        Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show()
       /* if (TextUtils.isEmpty(edit_ip_address.text.toString())) {
            edit_ip_address.error = "Введите ip-адрес."
            edit_ip_address.requestFocus()
            return
        }*/
    }
}
