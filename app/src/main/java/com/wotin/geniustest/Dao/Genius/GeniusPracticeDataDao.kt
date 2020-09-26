package com.wotin.geniustest.Dao.Genius

import androidx.room.*
import com.wotin.geniustest.CustomClass.GeniusPractice.GeniusPracticeDataCustomClass

@Dao
interface GeniusPracticeDataDao {
    @Query("select * from GeniusPracticeData")
    fun getAll() : GeniusPracticeDataCustomClass

    @Update
    fun updateGeniusPracticeData(geniusPracticeData: GeniusPracticeDataCustomClass)

    @Insert
    fun insertGeniusPracticeData(geniusPracticeData : GeniusPracticeDataCustomClass)

    @Query("DELETE FROM GeniusPracticeData")
    fun deleteGeniusPracticeData()
}