package ru.vladamamutova.remotestick.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import ru.vladamamutova.remotestick.ui.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_control.*
import ru.vladamamutova.remotestick.R
import ru.vladamamutova.remotestick.ui.fragments.KeyboardFragment


class ControlActivity : AppCompatActivity() {

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
    }

    private fun setupViewPager(viewPager : ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(KeyboardFragment())
        adapter.addFragment(KeyboardFragment())
        viewPager.adapter = adapter
    }
}
