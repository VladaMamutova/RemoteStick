package ru.vladamamutova.remotestick.ui.activities

import android.content.Intent
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnLongClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
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
import ru.vladamamutova.remotestick.ui.fragments.SpecialKeyboardFragment
import ru.vladamamutova.remotestick.utils.OnBackPressedListener
import kotlin.concurrent.thread


class ControlActivity : AppCompatActivity(), OnBackPressedListener {
    private var disconnectionToast: Toast? = null
    private var isKeyboardVisible: Boolean = false

    private val longClickListener = OnLongClickListener {
        when (it.id) {
            R.id.ctrlButton -> {

            }
            R.id.shiftButton -> {

            }
            R.id.altButton -> {

            }
            R.id.winButton -> {

            }
            R.id.searchButton -> {

            }
            R.id.explorerButton -> {

            }
        }
        it.isSelected = !it.isSelected
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        viewPager.setupAdapter()
        tabs.setupWithViewPager(viewPager)
        val iconColors = resources.obtainTypedArray(R.array.icon_colors)
        tabs.setupIcons(resources.obtainTypedArray(R.array.icons), iconColors)

        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // При выборе вкладки открываем панель инструментов.
                if (viewPager.visibility == View.GONE) {
                    viewPager.visibility = View.VISIBLE
                }
                if (tab.position == 1) {
                    showKeyboard()
                }
                tab.setIconTintList(iconColors.getResourceId(tab.position, R.color.violet))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.position == 1) {
                    hideKeyboard()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    hideKeyboard()
                }
                // При повторном выборе вкладки скрываем панель инструментов.
                viewPager.visibility = View.GONE
                tab.setIconTintList(R.color.grey)
                // Устанавливаем текущую вкладку в null, чтобы
                // не обрабатывать это событие, когда панель уже была скрыта.
                tabs.selectTab(null)
            }
        })

        touchpad.setOnMouseActionListener(
            RemoteStickClient.myInstance.mousePlugin
        )

        keyView.setOnBackPressedListener(this)
        keyView.setKeyboardListener(RemoteStickClient.myInstance.keyboardPlugin)

        setSpecialKeysLongClickListener()

        thread {
            RemoteStickClient.myInstance.run()
            // Здесь клиент завершил работу.
            // Если есть сообщение об ошибке (то есть сервер перестал отвечать),
            // то отображаем сообщение и завершаем активность.
            if (RemoteStickClient.myInstance.errorMessage != "") {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext, RemoteStickClient.myInstance.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (isKeyboardVisible) {
                        hideKeyboard()
                    }

                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(R.anim.right_in, R.anim.right_out)
                    finish()
                }
            }
        }
    }

    private fun ViewPager.setupAdapter() {
        // Устанавливаем высоту панели инструментов в 39% (высота клавиатуры).
        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.39).toInt()
        )

        // Добавляем вкладки.
        val adapter = ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(MediaFragment())
            addFragment(KeyboardFragment())
            addFragment(SpecialKeyboardFragment())
            addFragment(MediaFragment())
            addFragment(MediaFragment())
            addFragment(MediaFragment())
            addFragment(MediaFragment())
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

    private fun setSpecialKeysLongClickListener() {
        ctrlButton.setOnLongClickListener(longClickListener)
        shiftButton.setOnLongClickListener(longClickListener)
        altButton.setOnLongClickListener(longClickListener)
        winButton.setOnLongClickListener(longClickListener)
        searchButton.setOnLongClickListener(longClickListener)
        explorerButton.setOnLongClickListener(longClickListener)
    }

    private fun showKeyboard() {
        if (!isKeyboardVisible) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            keyView.requestFocus()
            imm.showSoftInput(keyView, InputMethodManager.SHOW_FORCED)
            isKeyboardVisible = true
        }
    }

    private fun hideKeyboard() {
        if (isKeyboardVisible) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            keyView.clearFocus()
            imm.hideSoftInputFromWindow(keyView.windowToken, 0)
            isKeyboardVisible = false
        }
    }

    override fun onBackPressed() {
        doBack()
    }

    override fun doBack() {
        if (disconnectionToast == null) {
            disconnectionToast = Toast.makeText(
                this,
                "Нажмите снова для отключения", Toast.LENGTH_SHORT
            )
            disconnectionToast!!.show()
        } else {
            // Отключаем клиент после повторного нажатия кнопки "Назад"
            // (то есть когда предыдущее уведомление ещё отображается).
            if (disconnectionToast!!.view.isShown) {
                disconnectionToast!!.cancel()

                RemoteStickClient.myInstance.stop()
                if (isKeyboardVisible) {
                    hideKeyboard()
                }

                startActivity(Intent(applicationContext, MainActivity::class.java))
                overridePendingTransition(R.anim.right_in, R.anim.right_out)
                finish()
            } else {
                disconnectionToast!!.show()
            }
        }
    }

    fun onLeftClick(view: View) {
        RemoteStickClient.myInstance.mousePlugin.onLeftClick()
    }

    fun onMiddleClick(view: View) {
        RemoteStickClient.myInstance.mousePlugin.onMiddleClick()
    }

    fun onRightClick(view: View) {
        RemoteStickClient.myInstance.mousePlugin.onRightClick()
    }

    fun onSpecialKeysButtonClick(view: View) {
        if (specialKeysPanel.visibility == View.GONE) {
            specialKeysPanel.visibility = View.VISIBLE
            (view as ImageButton).isSelected = true
        } else {
            specialKeysPanel.visibility = View.GONE
            (view as ImageButton).isSelected = false
        }
    }

    fun onSpecialKeyClick(view: View) {
        when (view.id) {
            R.id.ctrlButton -> {
                RemoteStickClient.myInstance.keyboardPlugin.pressCtrl()
            }
            R.id.shiftButton -> {
                RemoteStickClient.myInstance.keyboardPlugin.pressShift()
            }
            R.id.altButton -> {
                RemoteStickClient.myInstance.keyboardPlugin.pressAlt()
            }
            R.id.winButton -> {
                RemoteStickClient.myInstance.keyboardPlugin.pressWin()
            }
            R.id.searchButton -> {
                RemoteStickClient.myInstance.keyboardPlugin.sendSearchBarKeys()
            }
            R.id.explorerButton -> {
                RemoteStickClient.myInstance.keyboardPlugin.sendExplorerKeys()
            }
        }
    }
}
