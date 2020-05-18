package ru.vladamamutova.remotestick.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_device.view.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.model.Device

class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    private var devices: List<Device>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Получаем элементы представления без использования
        // дорогостоящего вызова findViewById()
        // с помощью расширения Kotlin Android
        val name: TextView = itemView.name
        val ip: TextView = itemView.ip
    }

    fun setData(devices: List<Device>) {
        this.devices = devices.asReversed()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devices?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        devices?.get(position)?.let { message ->
            holder.name.text = message.name
            holder.ip.text = message.ip
        }
    }
}
