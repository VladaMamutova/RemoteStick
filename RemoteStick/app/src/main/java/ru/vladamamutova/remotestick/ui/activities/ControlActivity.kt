package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.content.res.TypedArray
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

        viewPager.setupAdapter()
        tabs.setupWithViewPager(viewPager)
        val iconColors = resources.obtainTypedArray(R.array.icon_colors)
        tabs.setupIcons(resources.obtainTypedArray(R.array.icons), iconColors)

        tabs.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // При выборе вкладки открываем панель инструментов.
                if (viewPager.visibility == View.GONE) {
                    viewPager.visibility = View.VISIBLE
                }
                tab.setIconTintList(iconColors.getResourceId(tab.position, R.color.violet))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                // При повторном выборе вкладки скрываем панель инструментов.
                viewPager.visibility = View.GONE
                tab.setIconTintList(R.color.grey)
                // Устанавливаем текущую вкладку в null, чтобы
                // не обрабатывать это событие, когда панель уже была скрыта.
                tabs.selectTab(null)
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

    private fun ViewPager.setupAdapter() {
        // Устанавливаем высоту панели инструментов в 30%.
        this.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.3).toInt()
        ).apply { addRule(RelativeLayout.BELOW, R.id.tabs); }

        // Добавляем вкладки.
        val adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(KeyboardFragment())
            addFragment(MediaFragment())
            addFragment(KeyboardFragment())
            addFragment(KeyboardFragment())
            addFragment(KeyboardFragment())
            addFragment(KeyboardFragment())
            addFragment(KeyboardFragment())
        }
        this.adapter = adapter
    }

    private fun TabLayout.setupIcons(icons: TypedArray, colors: TypedArray) {
        for (i in 0 until this.tabCount) {
            this.getTabAt(i)!!.setIcon(icons.getResourceId(i, R.drawable.ic_keyboard))
            this.getTabAt(i)!!.setIconTintList(colors.getResourceId(i, R.color.violet))
        }
    }

    private fun TabLayout.Tab.setIconTintList(colorResource: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.icon?.setTintList(
                ContextCompat.getColorStateList(
                    applicationContext, colorResource
                )
            )
        }
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

    fun onRightButtonClick(view: View) {}
    fun onLeftButtonClick(view: View) {}
}
