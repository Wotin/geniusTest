package com.wotin.geniustest

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.wotin.geniustest.CustomClass.GeniusPractice.GeniusPracticeDataCustomClass
import com.wotin.geniustest.CustomClass.GeniusTest.GeniusTestDataCustomClass
import com.wotin.geniustest.CustomClass.UserCustomClass
import com.wotin.geniustest.DB.GeniusPracticeDataDB
import com.wotin.geniustest.DB.GeniusTestDataDB
import com.wotin.geniustest.DB.UserDB
import okhttp3.internal.connection.ConnectInterceptor
import java.lang.Exception

lateinit var geniusPracticeDB : GeniusPracticeDataDB

fun deleteUserDataAndGeniusTestAndPracticeData(context: Context) {
    deleteUserData(context)
    deleteGeniusPracticeData(context)
    deleteGeniusTestData(context)
}


fun deleteGeniusPracticeData(context: Context) {
    val geniusPracticeDB : GeniusPracticeDataDB = Room.databaseBuilder(
        context,
        GeniusPracticeDataDB::class.java, "geniusPractice.db"
    )
        .allowMainThreadQueries()
        . fallbackToDestructiveMigration ()
        .build()
    geniusPracticeDB.geniusPracticeDataDB().deleteGeniusPracticeData()
}

fun deleteGeniusTestData(context: Context) {
    val geniusTestDB : GeniusTestDataDB = Room.databaseBuilder(
        context,
        GeniusTestDataDB::class.java, "geniusTest.db"
    )
        .allowMainThreadQueries()
        . fallbackToDestructiveMigration ()
        .build()
    geniusTestDB.geniusTestDataDB().deleteGeniusTestData()
}


fun insertGeniusTestData(
    geniusTestData : GeniusTestDataCustomClass,
    context: Context
) {
    val geniusTestDataDB: GeniusTestDataDB = Room.databaseBuilder(
        context,
        GeniusTestDataDB::class.java, "geniusTest.db"
    )
        .allowMainThreadQueries()
        . fallbackToDestructiveMigration ()
        .build()
    geniusTestDataDB.geniusTestDataDB().insertGeniusTestData(
        geniusTestData
    )
}

fun insertGeniusPracticeData(
    geniusPracticeData : GeniusPracticeDataCustomClass,
    context: Context
) {
    val geniusPracticeDataDB : GeniusPracticeDataDB = Room.databaseBuilder(
        context,
        GeniusPracticeDataDB::class.java, "geniusPractice.db"
    )
        .allowMainThreadQueries()
        . fallbackToDestructiveMigration ()
        .build()
    geniusPracticeDataDB.geniusPracticeDataDB().insertGeniusPracticeData(
        geniusPracticeData
    )
    Log.d("TAG", "insertGeniusPracticeData is $geniusPracticeData")
}

fun getGeniusTestData(
    context: Context
): GeniusTestDataCustomClass {
    val geniusTestDataDB : GeniusTestDataDB = Room.databaseBuilder(
        context,
        GeniusTestDataDB::class.java, "geniusTest.db"
    )
        .allowMainThreadQueries()
        . fallbackToDestructiveMigration ()
        .build()
    return geniusTestDataDB.geniusTestDataDB().getAll()
}



fun getGeniusPracticeData(
    context: Context
): GeniusPracticeDataCustomClass {
    try {
        geniusPracticeDB = Room.databaseBuilder(
            context,
            GeniusPracticeDataDB::class.java, "geniusPractice.db"
        )
            .allowMainThreadQueries()
            . fallbackToDestructiveMigration ()
            .build()
    } catch (e : Exception){
        Log.d("TAG", "getGeniusPracticeData: error is $e")
    }

    Log.d("TAG", "getGeniusPracticeData is ${geniusPracticeDB.geniusPracticeDataDB().getAll()}")

    return geniusPracticeDB.geniusPracticeDataDB().getAll()
}

fun updateGeniusPracticeData(context: Context, geniusPracticeData: GeniusPracticeDataCustomClass) {
    val geniusPracticeDB : GeniusPracticeDataDB = Room.databaseBuilder(
        context,
        GeniusPracticeDataDB::class.java, "geniusPractice.db"
    )
        .allowMainThreadQueries()
        . fallbackToDestructiveMigration ()
        .build()

    geniusPracticeDB.geniusPracticeDataDB().updateGeniusPracticeData(geniusPracticeData = geniusPracticeData)

}
