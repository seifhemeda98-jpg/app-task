package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.StudentTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentTaskDao {
    @Query("SELECT * FROM student_tasks ORDER BY isCompleted ASC, dueDateMillis ASC, id DESC")
    fun getAllTasks(): Flow<List<StudentTaskEntity>>

    @Query("SELECT * FROM student_tasks WHERE subjectId = :subjectId ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getTasksForSubject(subjectId: Long): Flow<List<StudentTaskEntity>>

    @Query("SELECT * FROM student_tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: Long): Flow<StudentTaskEntity?>

    @Query("SELECT * FROM student_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskByIdOnce(id: Long): StudentTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudentTaskEntity): Long

    @Update
    suspend fun updateTask(task: StudentTaskEntity)

    @Query("UPDATE student_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)

    @Delete
    suspend fun deleteTask(task: StudentTaskEntity)

    @Query("DELETE FROM student_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("SELECT COUNT(*) FROM student_tasks WHERE isCompleted = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM student_tasks")
    suspend fun getCount(): Int
}
