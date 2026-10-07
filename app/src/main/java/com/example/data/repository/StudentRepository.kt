package com.example.data.repository

import com.example.data.local.dao.ScheduleSlotDao
import com.example.data.local.dao.StudentTaskDao
import com.example.data.local.dao.SubjectDao
import com.example.data.local.entity.ScheduleSlotEntity
import com.example.data.local.entity.StudentTaskEntity
import com.example.data.local.entity.SubjectEntity
import com.example.data.model.SlotWithSubject
import com.example.data.model.TaskWithSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class StudentRepository(
    private val subjectDao: SubjectDao,
    private val scheduleSlotDao: ScheduleSlotDao,
    private val studentTaskDao: StudentTaskDao
) {
    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = subjectDao.getAllSubjects()

    suspend fun getSubjectById(id: Long): SubjectEntity? = subjectDao.getSubjectByIdOnce(id)

    suspend fun insertSubject(subject: SubjectEntity): Long = subjectDao.insertSubject(subject)

    suspend fun updateSubject(subject: SubjectEntity) = subjectDao.updateSubject(subject)

    suspend fun deleteSubject(subject: SubjectEntity) = subjectDao.deleteSubject(subject)

    // Schedule Slots
    val allSlots: Flow<List<ScheduleSlotEntity>> = scheduleSlotDao.getAllSlots()

    val allSlotsWithSubjects: Flow<List<SlotWithSubject>> = combine(
        scheduleSlotDao.getAllSlots(),
        subjectDao.getAllSubjects()
    ) { slots, subjects ->
        val subjectMap = subjects.associateBy { it.id }
        slots.map { slot ->
            SlotWithSubject(slot, subjectMap[slot.subjectId])
        }
    }

    fun getSlotsForDay(dayOfWeek: Int): Flow<List<ScheduleSlotEntity>> =
        scheduleSlotDao.getSlotsForDay(dayOfWeek)

    suspend fun getSlotsForSubject(subjectId: Long): List<ScheduleSlotEntity> =
        scheduleSlotDao.getSlotsForSubjectOnce(subjectId)

    suspend fun insertSlot(slot: ScheduleSlotEntity): Long = scheduleSlotDao.insertSlot(slot)

    suspend fun deleteSlot(slot: ScheduleSlotEntity) = scheduleSlotDao.deleteSlot(slot)

    suspend fun deleteSlotsForSubject(subjectId: Long) = scheduleSlotDao.deleteSlotsForSubject(subjectId)

    // Tasks
    val allTasks: Flow<List<StudentTaskEntity>> = studentTaskDao.getAllTasks()

    val allTasksWithSubjects: Flow<List<TaskWithSubject>> = combine(
        studentTaskDao.getAllTasks(),
        subjectDao.getAllSubjects()
    ) { tasks, subjects ->
        val subjectMap = subjects.associateBy { it.id }
        tasks.map { task ->
            TaskWithSubject(task, subjectMap[task.subjectId])
        }
    }

    suspend fun getTaskById(id: Long): StudentTaskEntity? = studentTaskDao.getTaskByIdOnce(id)

    suspend fun insertTask(task: StudentTaskEntity): Long = studentTaskDao.insertTask(task)

    suspend fun updateTask(task: StudentTaskEntity) = studentTaskDao.updateTask(task)

    suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) =
        studentTaskDao.updateTaskCompletion(id, isCompleted)

    suspend fun deleteTask(task: StudentTaskEntity) = studentTaskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = studentTaskDao.deleteTaskById(id)
}
