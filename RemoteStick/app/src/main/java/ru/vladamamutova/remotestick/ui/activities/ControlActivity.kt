package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_control.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.service.RemoteStickClient
import ru.vladamamutova.remotestick.ui.adapters.ViewPagerAdapter
import ru.vladamamutova.remotestick.ui.fragments.KeyboardFragment
import ru.vladamamutova.remotestick.ui.fragments.MediaFragment
import kotlin.concurrent.thread


class ControlActivity : AppCompatActivity() {
    private var disconnectionToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        viewPager.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.3).toInt())
            .apply { addRule(RelativeLayout.BELOW, R.id.tabs); }

        setupViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_keyboard)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_music_video)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_keyboard)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_music_video)
        tabs.getTabAt(4)!!.setIcon(R.drawable.ic_music_video)
        tabs.getTabAt(5)!!.setIcon(R.drawable.ic_music_video)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tabs.getTabAt(0)!!.setIconTintList(R.color.violet_tab_selector)
            tabs.getTabAt(1)!!.setIconTintList(R.color.blue_tab_selector)
            tabs.getTabAt(2)!!.setIconTintList(R.color.green_tab_selector)
            tabs.getTabAt(3)!!.setIconTintList(R.color.pink_tab_selector)
            tabs.getTabAt(4)!!.setIconTintList(R.color.violet_tab_selector)
            tabs.getTabAt(5)!!.setIconTintList(R.color.blue_tab_selector)
        }

        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // При выборе вкладки в любом случае панель инструментов
                // должна быть открыта.
                if (viewPager.visibility == View.GONE) {
                    viewPager.visibility = View.VISIBLE
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when(tab.position) {
                        0 -> tab.setIconTintList(R.color.violet_tab_selector)
                        1 -> tab.setIconTintList(R.color.blue_tab_selector)
                        2 -> tab.setIconTintList(R.color.green_tab_selector)
                        3 -> tab.setIconTintList(R.color.pink_tab_selector)
                        4 -> tab.setIconTintList(R.color.violet_tab_selector)
                        5 -> tab.setIconTintList(R.color.blue_tab_selector)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                // При повторном выборе вкладки скрываем
                // панель инструментов, если она была открыта.
                if (viewPager.visibility == View.VISIBLE) {
                    viewPager.visibility = View.GONE
                    tab.setIconTintList(R.color.grey)
                    tabs.selectTab(null)
                } /*else {
                    viewPager.visibility = View.VISIBLE
                }*/
            }
        })

        thread {
            RemoteStickClient.myInstance.run()
            // Здесь клиент завершил работу.
            // Если есть сообщение об ошибке (то есть сервер перестал отвечать),
            // то отобржаем сообщение и завершаем активность.
            if (RemoteStickClient.myInstance.errorMessage != "") {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext, RemoteStickClient.myInstance.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun TabLayout.Tab.setIconTintList(colorResource: Int) {
        this.icon?.setTintList(
            ContextCompat.getColorStateList(
                applicationContext, colorResource
            )
        )
    }
    private fun setupViewPager(viewPager : ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(MediaFragment())
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

                RemoteStickClient.myInstance.stop()

                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                disconnectionToast!!.show()
            }
        }
    }
}
