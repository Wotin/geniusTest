package com.wotin.geniustest.adapter.test

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wotin.geniustest.AppStorage
import com.wotin.geniustest.activity.test.TestConcentractionActivity
import com.wotin.geniustest.activity.test.TestMemoryActivity
import com.wotin.geniustest.customClass.geniusTest.TestModeCustomClass
import com.wotin.geniustest.R
import com.wotin.geniustest.databinding.TestModeRecyclerviewItemBinding
import com.wotin.geniustest.roomMethod.UpdateRoomMethod
import com.wotin.geniustest.networkState
import kotlin.collections.ArrayList

class TestModeRecyclerViewAdapter(val modeList : ArrayList<TestModeCustomClass>, val modeClickedInterface : ModeClickedInterface) : RecyclerView.Adapter<TestModeRecyclerViewAdapter.CustomViewHolder>() {

    interface ModeClickedInterface {
        fun modeClicked(mode : String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view = TestModeRecyclerviewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CustomViewHolder(
            view
        ).apply {
            modeLayout.setOnClickListener{
                val storage = AppStorage(parent.context)
                val connectivityManager : ConnectivityManager = parent.context.getSystemService(
                    Context.CONNECTIVITY_SERVICE
                ) as ConnectivityManager
                if(networkState(connectivityManager)) {
                    Log.d("TAG", "onCreateViewHolder: adapterPosition is $adapterPosition")
                    if(modeList[adapterPosition].start) {
                        when(modeList[adapterPosition].mode) {
                            "기억력 테스트" -> {
                                if (!storage.purchasedUnlimitedTry()) modeList[adapterPosition].start = false
                                val intent = Intent(parent.context, TestMemoryActivity::class.java)
                                parent.context.startActivity(intent)
                                (parent.context as Activity).finish()
                                UpdateRoomMethod().updateTestModeData(parent.context.applicationContext, modeList[adapterPosition])
                            }
                            "집중력 테스트" -> {
                                if (!storage.purchasedUnlimitedTry()) modeList[adapterPosition].start = false
                                val intent = Intent(parent.context, TestConcentractionActivity::class.java)
                                parent.context.startActivity(intent)
                                (parent.context as Activity).finish()
                                UpdateRoomMethod().updateTestModeData(parent.context.applicationContext, modeList[adapterPosition])
                            }
                        }
                        modeClickedInterface.modeClicked(modeList[adapterPosition].mode)
                    } else {
                        Toast.makeText(parent.context.applicationContext, "10분에 한번 도전할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(parent.context.applicationContext, "네트워크 연결을 확인해주세요", Toast.LENGTH_LONG).show()

            }
            modeQuestionMark.setOnClickListener {
                when(modeList[adapterPosition].mode) {
                    "기억력 테스트" -> {
                        val dialog = AlertDialog.Builder(parent.context)
                        val EDialog = LayoutInflater.from(parent.context)
                        val MView = EDialog.inflate(R.layout.explain_memory_dialog_layout, null)
                        val builder = dialog.create()

                        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        builder.window?.requestFeature(Window.FEATURE_NO_TITLE)

                        builder.setView(MView)
                        builder.show()
                    }
                    "집중력 테스트" -> {
                        val dialog = AlertDialog.Builder(parent.context)
                        val EDialog = LayoutInflater.from(parent.context)
                        val MView = EDialog.inflate(R.layout.explain_concentraction_dialog_layout, null)
                        val builder = dialog.create()

                        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        builder.window?.requestFeature(Window.FEATURE_NO_TITLE)

                        builder.setView(MView)
                        builder.show()
                    }
                    "순발력 테스트" -> {
                        val dialog = AlertDialog.Builder(parent.context)
                        val EDialog = LayoutInflater.from(parent.context)
                        val MView = EDialog.inflate(R.layout.explain_quickness_dialog_layout, null)
                        val builder = dialog.create()
                        builder.setView(MView)
                        builder.show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = modeList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.onBind(modeList[position])
    }

    class CustomViewHolder(val binding : TestModeRecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val modeLayout = itemView.findViewById<CardView>(R.id.test_mode_item_layout)
        val modeQuestionMark = itemView.findViewById<ImageView>(R.id.test_mode_question_mark_imageview)
        fun onBind(data: TestModeCustomClass) {
            binding.modeData = data
        }
    }
}