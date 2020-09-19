package com.wotin.geniustest.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.wotin.geniustest.*
import com.wotin.geniustest.Activity.LoginAndSignUp.LoginActivity
import com.wotin.geniustest.Activity.UserManagement.DeleteUserActivity
import com.wotin.geniustest.Activity.UserManagement.UserInformationActivity
import com.wotin.geniustest.Adapter.TabLayoutFragmentPagerAdapter
import com.wotin.geniustest.Service.ConcentractionTestHeartManagementService
import com.wotin.geniustest.Service.QuicknessTestHeartManagementService
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 3초마다 윈도우 조정해주는 메소드 실행.
        controlWindowOnTimer()

        navigation_button.setOnClickListener {
            layout_drawer.openDrawer(GravityCompat.START)
        }

        navigation_view.setNavigationItemSelectedListener(this)
        setNavigationHeaderLayout()


        fragment_view_pager.adapter =
            TabLayoutFragmentPagerAdapter(
                supportFragmentManager
            )
        tab_layout.setupWithViewPager(fragment_view_pager)
        for(i in 0 until tab_layout.tabCount) {
            when(i) {
                0 -> {
                    tab_layout.getTabAt(i)!!.setIcon(R.drawable.practice)
                    tab_layout.getTabAt(i)!!.text = "연습하기"
                }
                1 -> {
                    tab_layout.getTabAt(i)!!.setIcon(R.drawable.genius)
                    tab_layout.getTabAt(i)!!.text = "테스트"
                }
                2 -> {
                    tab_layout.getTabAt(i)!!.setIcon(R.drawable.ranking)
                    tab_layout.getTabAt(i)!!.text = "랭킹"
                }
            }
        }
    }

    //3초마다 윈도우를 조정해주는 메소드.
    private fun controlWindowOnTimer() {
        timer(period = 3000)
        {
            runOnUiThread {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                deleteUserDataAndGeniusTestAndPracticeData(applicationContext)
                deleteUserDataSharedPreference()
                deleteTestModeData(applicationContext)
                stopService(Intent(this, ConcentractionTestHeartManagementService::class.java))
                stopService(Intent(this, QuicknessTestHeartManagementService::class.java))
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.secession -> {
                val intent = Intent(this, DeleteUserActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        layout_drawer.closeDrawers()
        return true
    }

    private fun deleteUserDataSharedPreference() {
        val pref = getPreferences(0)
        val editor = pref.edit()

        editor
            .remove("UID")
            .remove("id")
            .remove("password")
            .apply()
    }

    private fun setNavigationHeaderLayout() {
        val headerView = LayoutInflater.from(this).inflate(R.layout.navigation_view_header_layout, null)
        val userLevelImageView = headerView.findViewById<ImageView>(R.id.user_level_imageview_header_layout)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name_textview_header_layout)
        val userIdTextView = headerView.findViewById<TextView>(R.id.user_id_textview_header_layout)
        val userLevelTextView = headerView.findViewById<TextView>(R.id.user_level_textview_header_layout)
        val userData = getUserData(applicationContext)
        val userGeniusTestData = getGeniusTestData(applicationContext)
        headerView.setOnClickListener {
            val intent = Intent(this, UserInformationActivity::class.java)
            intent.putExtra("userId", userData.id)
            startActivity(intent)
            finish()
        }
        userNameTextView.text = userData.name
        userIdTextView.text = EncryptionAndDetoxification().encryptionAndDetoxification(userData.id)
        userLevelTextView.text = userGeniusTestData.level
        when(userLevelTextView.text.toString()) {
            "초보" -> userLevelImageView.setImageResource(R.drawable.bad_brain)
            "중수" -> userLevelImageView.setImageResource(R.drawable.normal_brain)
            "고수" -> userLevelImageView.setImageResource(R.drawable.good_brain)
            "천재" -> userLevelImageView.setImageResource(R.drawable.genius)
        }
        navigation_view.addHeaderView(headerView)
    }

}