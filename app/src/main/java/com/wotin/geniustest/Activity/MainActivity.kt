package com.wotin.geniustest.Activity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonArray
import com.wotin.geniustest.*
import com.wotin.geniustest.Activity.LoginAndSignUp.LoginActivity
import com.wotin.geniustest.Activity.UserManagement.DeleteUserActivity
import com.wotin.geniustest.Activity.UserManagement.UserInformationActivity
import com.wotin.geniustest.Adapter.TabLayoutFragmentPagerAdapter
import com.wotin.geniustest.Adapter.UserInformationRecyclerViewAdapter
import com.wotin.geniustest.Converters.MapJsonConverter
import com.wotin.geniustest.CustomClass.UserInformationCustomClass
import com.wotin.geniustest.Receiver.TestHeartManagementReceiver
import com.wotin.geniustest.RetrofitInterface.RetrofitAboutHeart
import com.wotin.geniustest.RoomMethod.DeleteRoomMethod
import com.wotin.geniustest.RoomMethod.GetRoomMethod
import com.wotin.geniustest.RoomMethod.UserRoomMethod
import com.wotin.geniustest.Service.ConcentractionTestHeartManagementService
import com.wotin.geniustest.Service.MemoryTestHeartManagementService
import com.wotin.geniustest.Service.QuicknessTestHeartManagementService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {

    lateinit var retrofit: Retrofit
    lateinit var getAboutHeart : RetrofitAboutHeart
    lateinit var okHttpClient: OkHttpClient
    val baseUrl = "http://118.32.174.85:8080"


    lateinit var heartAlertDialog : AlertDialog.Builder
    lateinit var heartEDialog: LayoutInflater
    lateinit var heartMView : View
    lateinit var heartBuilder : AlertDialog
    lateinit var heartUserRecyclerView : RecyclerView
    lateinit var heartUserTextView : TextView
    lateinit var heartUserRefreshLayout : SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        getAboutHeart = retrofit.create(RetrofitAboutHeart::class.java)

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
                DeleteRoomMethod().deleteUserDataAndGeniusTestAndPracticeData(applicationContext)
                deleteUserDataSharedPreference()
                DeleteRoomMethod().deleteTestModeData(applicationContext)
                stopService(Intent(this, MemoryTestHeartManagementService::class.java))
                stopService(Intent(this, ConcentractionTestHeartManagementService::class.java))
                stopService(Intent(this, QuicknessTestHeartManagementService::class.java))
                cancelAlarm()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.secession -> {
                val intent = Intent(this, DeleteUserActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.heart_to_me -> {
                setHeartToMeDialog()
            }

            R.id.heart_to_people -> {
                setHeartToPeopleDialog()
            }

        }
        layout_drawer.closeDrawers()
        return true
    }

    private fun setHeartToMeDialog() {
        heartAlertDialog = AlertDialog.Builder(this@MainActivity)
        heartEDialog = LayoutInflater.from(this@MainActivity)
        heartMView = heartEDialog.inflate(R.layout.heart_user_information_dialog, null)
        heartBuilder = heartAlertDialog.create()
        heartUserRecyclerView = heartMView.findViewById<RecyclerView>(R.id.heart_user_information_recyclerview)
        heartUserTextView = heartMView.findViewById<TextView>(R.id.heart_user_information_tome_or_topeople_textview)
        heartUserRefreshLayout = heartMView.findViewById(R.id.heart_user_refresh_layout)
        Thread.sleep(100)
        heartUserRefreshLayout.setOnRefreshListener {
            getHeartToMe()
            heartUserRefreshLayout.isRefreshing = false
        }
        heartUserTextView.text = "나를 좋아하는 사람"
        getHeartToMe()
        heartBuilder.setView(heartMView)
        heartBuilder.show()
    }

    private fun getHeartToMe() {
        getAboutHeart.getHeartToMePeople(UserRoomMethod().getUserData(applicationContext).UniqueId).enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d("TAG", "getHeartToMe Failure, $t")
                Toast.makeText(applicationContext, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                Log.d("TAG", "getHeartToMe data is ${response.body()!!}")
                val heartToMeList : ArrayList<UserInformationCustomClass> = arrayListOf()
                for(i in response.body()!!) {
                    val mapI = MapJsonConverter().MapToJsonConverter(i.toString())
                    heartToMeList.add(UserInformationCustomClass(
                        level = mapI["level"].toString(), id = mapI["id"].toString(), heartNum = mapI["heart_number"].toString().toFloat().toInt().toString(),
                        testSumDifference = mapI["genius_difference"].toString() + "%"))
                }
                heartToMeList.forEach { heartToMe ->
                    Log.d("TAG", "onResponse: hearToMeList Element is ${heartToMe.level} ${heartToMe.id} ${heartToMe.heartNum} ${heartToMe.testSumDifference}")
                }
                val heartUserRecyclerViewAdapter = UserInformationRecyclerViewAdapter(heartToMeList)
                heartUserRecyclerView.apply {
                    adapter = heartUserRecyclerViewAdapter
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                }
                heartBuilder.setView(heartMView)
                heartBuilder.show()
            }

        })
    }

    private fun setHeartToPeopleDialog() {
        heartAlertDialog = AlertDialog.Builder(this@MainActivity)
        heartEDialog = LayoutInflater.from(this@MainActivity)
        heartMView = heartEDialog.inflate(R.layout.heart_user_information_dialog, null)
        heartBuilder = heartAlertDialog.create()
        heartUserRecyclerView = heartMView.findViewById<RecyclerView>(R.id.heart_user_information_recyclerview)
        heartUserTextView = heartMView.findViewById<TextView>(R.id.heart_user_information_tome_or_topeople_textview)
        heartUserRefreshLayout = heartMView.findViewById(R.id.heart_user_refresh_layout)
        Thread.sleep(100)
        heartUserRefreshLayout.setOnRefreshListener {
            getHeartToPeople()
            heartUserRefreshLayout.isRefreshing = false
        }
        heartUserTextView.text = "내가 좋아하는 사람"
        getHeartToPeople()
        heartBuilder.setView(heartMView)
        heartBuilder.show()
    }

    private fun getHeartToPeople() {
        getAboutHeart.getHeartToPeople(UserRoomMethod().getUserData(applicationContext).UniqueId).enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d("TAG", "getHeartToMe Failure, $t")
                Toast.makeText(applicationContext, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                Log.d("TAG", "getHeartToMe data is ${response.body()!!}")
                val heartToPeopleList : ArrayList<UserInformationCustomClass> = arrayListOf()
                for(i in response.body()!!) {
                    val mapI = MapJsonConverter().MapToJsonConverter(i.toString())
                    heartToPeopleList.add(UserInformationCustomClass(
                        level = mapI["level"].toString(), id = mapI["id"].toString(), heartNum = mapI["heart_number"].toString().toFloat().toInt().toString(),
                        testSumDifference = mapI["genius_difference"].toString() + "%"))
                }
                heartToPeopleList.forEach { heartToPeople ->
                    Log.d("TAG", "onResponse: hearToMeList Element is ${heartToPeople.level} ${heartToPeople.id} ${heartToPeople.heartNum} ${heartToPeople.testSumDifference}")
                }
                val heartUserRecyclerViewAdapter = UserInformationRecyclerViewAdapter(heartToPeopleList)
                heartUserRecyclerView.apply {
                    adapter = heartUserRecyclerViewAdapter
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                }
            }

        })
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
        val userData = UserRoomMethod().getUserData(applicationContext)
        val userGeniusTestData = GetRoomMethod().getGeniusTestData(applicationContext)
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

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, TestHeartManagementReceiver::class.java)
        try {
            val memoryPendingIntent = PendingIntent.getBroadcast(this, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(memoryPendingIntent)
            memoryPendingIntent.cancel()
        } catch (e : Exception) {

        }
        try {
            val concentractionPendingIntent = PendingIntent.getBroadcast(this, 2, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(concentractionPendingIntent)
            concentractionPendingIntent.cancel()
        } catch (e : Exception) {

        }

        try {
            val quicknessPendingIntent = PendingIntent.getBroadcast(this, 3, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(quicknessPendingIntent)
            quicknessPendingIntent.cancel()
        } catch (e : Exception) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            heartBuilder.dismiss()
        } catch (e : Exception) {
            Log.d("TAG", "onDestroy: heartBuilder.dismiss() error is $e")
        }
    }

}