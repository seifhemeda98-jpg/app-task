package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ScheduleSlotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleSlotDao {
    @Query("SELECT * FROM schedule_slots ORDER BY dayOfWeek ASC, hour ASC, minute ASC")
    fun getAllSlots(): Flow<List<ScheduleSlotEntity>>

    @Query("SELECT * FROM schedule_slots WHERE dayOfWeek = :dayOfWeek ORDER BY hour ASC, minute ASC")
    fun getSlotsForDay(dayOfWeek: Int): Flow<List<ScheduleSlotEntity>>

    @Query("SELECT * FROM schedule_slots WHERE subjectId = :subjectId ORDER BY dayOfWeek ASC, hour ASC")
    fun getSlotsForSubject(subjectId: Long): Flow<List<ScheduleSlotEntity>>

    @Query("SELECT * FROM schedule_slots WHERE subjectId = :subjectId")
    suspend fun getSlotsForSubjectOnce(subjectId: Long): List<ScheduleSlotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: ScheduleSlotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<ScheduleSlotEntity>)

    @Update
    suspend fun updateSlot(slot: ScheduleSlotEntity)

    @Delete
    suspend fun deleteSlot(slot: ScheduleSlotEntity)

    @Query("DELETE FROM schedule_slots WHERE subjectId = :subjectId")
    suspend fun deleteSlotsForSubject(subjectId: Long)

    @Query("SELECT COUNT(*) FROM schedule_slots")
    suspend fun getCount(): Int
}
