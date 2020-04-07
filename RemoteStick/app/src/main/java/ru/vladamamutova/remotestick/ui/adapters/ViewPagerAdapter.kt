package ru.vladamamutova.remotestick.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Адаптер для ViewPager, который контролирует порядок вкладок,
 * заголовков и связанного с ними контента.
 */
class ViewPagerAdapter(manager: FragmentManager)
    : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: MutableList<Fragment> = mutableListOf()

    override fun getCount(): Int {
        return fragmentList.size
    }

    /**
     *  Получает фрагмер в зависимости от позиции вкладки.
     */
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    /**
     * Генерирует заголовок в зависимости от позиции вкладки.
     */
    override fun getPageTitle(position: Int): CharSequence? {
        // return null to display only the icon
        return null
    }

    /**
     * Добавляем новый фрагмент.
     */
    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}