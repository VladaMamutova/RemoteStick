package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_control.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.RemoteControlManager
import ru.vladamamutova.remotestick.ui.adapters.ViewPagerAdapter
import ru.vladamamutova.remotestick.ui.fragments.KeyboardFragment
import kotlin.concurrent.thread

class ControlActivity : AppCompatActivity() {
    private var disconnectionToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_keyboard)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_music_video)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_keyboard)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_music_video)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tabs.getTabAt(0)!!.icon!!.setTintList(ContextCompat.getColorStateList(this, R.color.violet_tab_selector))
            tabs.getTabAt(1)!!.icon!!.setTintList(ContextCompat.getColorStateList(this, R.color.blue_tab_selector))
            tabs.getTabAt(2)!!.icon!!.setTintList(ContextCompat.getColorStateList(this, R.color.green_tab_selector))
            tabs.getTabAt(3)!!.icon!!.setTintList(ContextCompat.getColorStateList(this, R.color.pink_tab_selector))
        }

        thread {
            RemoteControlManager.myInstance.run()
            if (RemoteControlManager.myInstance.errorMessage != "") {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext, RemoteControlManager.myInstance.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupViewPager(viewPager : ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(KeyboardFragment())
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        if(disconnectionToast == null) {
            disconnectionToast = Toast.makeText(this,
                "Нажмите снова для отключения", Toast.LENGTH_SHORT)
            disconnectionToast!!.show()
        } else {
            // Отключаем клиент после повторного нажатия кнопки "Назад"
            // (то есть когда предыдущее уведомление ещё отображается).
            if (disconnectionToast!!.view.isShown) {
                disconnectionToast!!.cancel()

                RemoteControlManager.myInstance.stop()

                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                disconnectionToast!!.show()
            }
        }
    }
}
