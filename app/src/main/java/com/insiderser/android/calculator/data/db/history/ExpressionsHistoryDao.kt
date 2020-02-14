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
package com.insiderser.android.calculator.data.db.history

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * A DAO (data access object) for performing CRUD (create, read, update, delete) operations
 * on [expressions history][ExpressionsHistoryEntity] table.
 */
@Dao
interface ExpressionsHistoryDao {

    /**
     * Get all entries from the database.
     * @return Paged list factory with all entities, or an empty list if the table is empty.
     */
    @Query("SELECT * FROM history")
    fun findAll(): DataSource.Factory<Int, ExpressionsHistoryEntity>

    /**
     * Get a single entry whose [id][ExpressionsHistoryEntity.id] matches the parameter [id].
     * @return Found [ExpressionsHistoryEntity], or `null` if nothing found.
     */
    @Query("SELECT * FROM history WHERE id == :id")
    suspend fun findOneById(id: Int): ExpressionsHistoryEntity

    /**
     * Insert a single [ExpressionsHistoryEntity] into the database.
     *
     * **Note**: [id][ExpressionsHistoryEntity.id] can be autogenerated by room.
     * Set the `id` to `0` to let room generate the id.
     *
     * @return The [id][ExpressionsHistoryEntity.id] of the inserted entry, or `-1` if not inserted.
     */
    @Insert
    suspend fun insertOne(entity: ExpressionsHistoryEntity): Long

    /**
     * Delete a single entry whose [id][ExpressionsHistoryEntity.id] matches the parameter `id`.
     * @return How many entries were deleted: `0` or `1`.
     */
    @Query("DELETE FROM history WHERE id == :id")
    suspend fun deleteOneById(id: Int): Int

    /**
     * Delete **all** entries in the table. **Be careful here!**
     * @return How many entries were deleted.
     */
    @Query("DELETE FROM history")
    suspend fun deleteAll(): Int
}
