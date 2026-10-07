package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ScheduleSlotDao
import com.example.data.local.dao.StudentTaskDao
import com.example.data.local.dao.SubjectDao
import com.example.data.local.entity.ScheduleSlotEntity
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        SubjectEntity::class,
        ScheduleSlotEntity::class,
        StudentTaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun scheduleSlotDao(): ScheduleSlotDao
    abstract fun studentTaskDao(): StudentTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_planner_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    seedDefaultData(database)
                }
            }
        }

        private suspend fun seedDefaultData(database: AppDatabase) {
            val subjectDao = database.subjectDao()
            val slotDao = database.scheduleSlotDao()
            val taskDao = database.studentTaskDao()

            // Seed default subjects
            val quranId = subjectDao.insertSubject(
                SubjectEntity(
                    name = "القرآن الكريم",
                    teacherName = "أ/ عبد الرحمن",
                    colorHex = "#16A34A",
                    notes = "حفظ ومراجعة وتجويد"
                )
            )

            val arabicId = subjectDao.insertSubject(
                SubjectEntity(
                    name = "اللغة العربية",
                    teacherName = "أ/ أحمد محمود",
                    colorHex = "#9333EA",
                    notes = "نحو، نصوص، وقراءة"
                )
            )

            val mathId = subjectDao.insertSubject(
                SubjectEntity(
                    name = "الرياضيات",
                    teacherName = "أ/ سامي خليل",
                    colorHex = "#2563EB",
                    notes = "جبر وهندسة"
                )
            )

            val scienceId = subjectDao.insertSubject(
                SubjectEntity(
                    name = "العلوم",
                    teacherName = "د/ طارق إبراهيم",
                    colorHex = "#0D9488",
                    notes = "فيزياء وكيمياء وأحياء"
                )
            )

            val englishId = subjectDao.insertSubject(
                SubjectEntity(
                    name = "اللغة الإنجليزية",
                    teacherName = "Mr. Omar",
                    colorHex = "#EA580C",
                    notes = "Grammar & Vocabulary"
                )
            )

            // Seed sample schedule slots (days: 1=Sat, 2=Sun, 3=Mon, 4=Tue, 5=Wed, 6=Thu)
            slotDao.insertSlots(
                listOf(
                    ScheduleSlotEntity(subjectId = quranId, dayOfWeek = 2, hour = 8, minute = 0, location = "فصل 1"),
                    ScheduleSlotEntity(subjectId = arabicId, dayOfWeek = 2, hour = 9, minute = 30, location = "فصل 1"),
                    ScheduleSlotEntity(subjectId = mathId, dayOfWeek = 2, hour = 11, minute = 0, location = "معمل الرياضيات"),
                    ScheduleSlotEntity(subjectId = englishId, dayOfWeek = 3, hour = 8, minute = 30, location = "فصل 2"),
                    ScheduleSlotEntity(subjectId = scienceId, dayOfWeek = 3, hour = 10, minute = 0, location = "المختبر العلمي"),
                    ScheduleSlotEntity(subjectId = mathId, dayOfWeek = 4, hour = 9, minute = 0, location = "معمل الرياضيات"),
                    ScheduleSlotEntity(subjectId = arabicId, dayOfWeek = 4, hour = 10, minute = 30, location = "فصل 1"),
                    ScheduleSlotEntity(subjectId = quranId, dayOfWeek = 5, hour = 8, minute = 0, location = "فصل 1")
                )
            )

            // Seed sample initial tasks with memorization and homework
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 1) // Tomorrow
            val tomorrowMillis = cal.timeInMillis

            taskDao.insertTask(
                StudentTaskEntity(
                    subjectId = quranId,
                    dueDateMillis = tomorrowMillis,
                    targetDayOfWeek = 2,
                    memorization = "حفظ سورة الملك من الآية 1 إلى الآية 15 مع أحكام النون الساكنة",
                    homework = "كتابة معاني الكلمات في الدفتر",
                    notes = "مراجعة متأنية مع الترتيل",
                    isCompleted = false,
                    remindNightBefore = true,
                    remindDayBefore = true,
                    remindOneHourBefore = true
                )
            )

            taskDao.insertTask(
                StudentTaskEntity(
                    subjectId = mathId,
                    dueDateMillis = tomorrowMillis,
                    targetDayOfWeek = 2,
                    memorization = "حفظ نظريات المثلث متساوي الساقين والنتائج الهندسية",
                    homework = "حل تمارين ص 42 مسائل رقم (1 و 3 و 5) في كشكول الواجب",
                    notes = "إحضار الأدوات الهندسية والمسطرة",
                    isCompleted = false,
                    remindNightBefore = true,
                    remindDayBefore = true,
                    remindOneHourBefore = true
                )
            )
        }
    }
}
