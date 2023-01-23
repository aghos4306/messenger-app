package com.aghogho.messengerappmuzz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aghogho.messengerappmuzz.databinding.ActivityMainBinding
import com.aghogho.messengerappmuzz.fragments.ChatsFragment
import com.aghogho.messengerappmuzz.fragments.SearchFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(binding.toolbarMain)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        if (::binding.isInitialized) {
            setSupportActionBar(binding.toolbarMain)
        }
        supportActionBar?.title = ""

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            //return super.getPageTitle(position)
            return titles[i]
        }

    }
}
