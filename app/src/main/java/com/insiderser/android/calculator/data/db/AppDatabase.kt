/*
 * Copyright 2020 Oleksandr Bezushko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.insiderser.android.calculator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.insiderser.android.calculator.BuildConfig.DEBUG
import com.insiderser.android.calculator.data.db.history.ExpressionsHistoryDao
import com.insiderser.android.calculator.data.db.history.ExpressionsHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * A main Room database. Here you can retrieve all app DAOs.
 *
 * You don't need to use this class directly. Instead, get one of DAOs using Dagger.
 */
@Database(
    entities = [
        ExpressionsHistoryEntity::class
    ],
    version = AppDatabase.DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val historyDao: ExpressionsHistoryDao

    companion object {

        const val DB_VERSION = 1
        private const val DB_NAME = "calculator.db"

        /**
         * Create an [AppDatabase] instance that is connected to the persistent SQLite
         * database. If the database doesn't exist, it will be created.
         *
         * **Note**: you don't need to use this method directly — use dagger for that.
         *
         * @param context an application context
         */
        @JvmStatic
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .setTransactionExecutor(Dispatchers.IO.asExecutor())
                .apply {
                    if (DEBUG) {
                        fallbackToDestructiveMigration()
                    }
                }
                .build()
    }
}
