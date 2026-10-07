package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ScheduleSlotEntity
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.DayOfWeekHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("مهام الطالب", appName)
    }

    @Test
    fun `insert and retrieve subject and task in database`() = runBlocking {
        val subjectDao = database.subjectDao()
        val taskDao = database.studentTaskDao()
        val slotDao = database.scheduleSlotDao()

        // Insert subject
        val subjectId = subjectDao.insertSubject(
            SubjectEntity(
                name = "الرياضيات",
                teacherName = "أ/ سامي",
                colorHex = "#2563EB",
                notes = "جبر وهندسة"
            )
        )
        assertTrue(subjectId > 0)

        // Insert schedule slot
        val slotId = slotDao.insertSlot(
            ScheduleSlotEntity(
                subjectId = subjectId,
                dayOfWeek = 2, // Sunday
                hour = 9,
                minute = 30,
                location = "معمل الرياضيات"
            )
        )
        assertTrue(slotId > 0)

        // Insert task with memorization and homework
        val taskId = taskDao.insertTask(
            StudentTaskEntity(
                subjectId = subjectId,
                dueDateMillis = System.currentTimeMillis(),
                targetDayOfWeek = 2,
                memorization = "حفظ قوانين حساب المثلثات",
                homework = "حل تمارين ص 50 رقم 1 إلى 5",
                notes = "إحضار الآلة الحاسبة",
                remindNightBefore = true,
                remindOneHourBefore = true
            )
        )
        assertTrue(taskId > 0)

        // Verify retrieval
        val tasks = taskDao.getAllTasks().first()
        assertEquals(1, tasks.size)
        val task = tasks.first()
        assertEquals("حفظ قوانين حساب المثلثات", task.memorization)
        assertEquals("حل تمارين ص 50 رقم 1 إلى 5", task.homework)
        assertEquals(false, task.isCompleted)

        // Toggle completion
        taskDao.updateTaskCompletion(taskId, true)
        val updatedTask = taskDao.getTaskByIdOnce(taskId)
        assertNotNull(updatedTask)
        assertEquals(true, updatedTask?.isCompleted)
    }

    @Test
    fun `day of week helper converts and formats correctly`() {
        assertEquals("السبت", DayOfWeekHelper.getDayName(1))
        assertEquals("الأحد", DayOfWeekHelper.getDayName(2))
        assertEquals("الجمعة", DayOfWeekHelper.getDayName(7))
        assertEquals("9:30 صباحاً", DayOfWeekHelper.formatTime(9, 30))
        assertEquals("1:15 مساءً", DayOfWeekHelper.formatTime(13, 15))
    }
}
