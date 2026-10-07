package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "student_tasks",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class StudentTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val dueDateMillis: Long, // تاريخ تسليم أو يوم الحصة
    val targetDayOfWeek: Int = 0, // اليوم المستهدف (1=السبت... 7=الجمعة)
    val memorization: String = "", // خانة التسميع أو الحفظ
    val homework: String = "", // خانة الواجب المنزلي
    val notes: String = "", // ملاحظات إضافية
    val isCompleted: Boolean = false, // حالة الإنجاز
    val remindNightBefore: Boolean = true, // تذكير بالليل
    val remindDayBefore: Boolean = true, // تذكير قبل اليوم المحدد
    val remindOneHourBefore: Boolean = true, // تذكير قبل موعد الحصة بساعة
    val createdAt: Long = System.currentTimeMillis()
)
