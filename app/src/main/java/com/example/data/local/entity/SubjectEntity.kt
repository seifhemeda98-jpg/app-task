package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val teacherName: String = "",
    val colorHex: String = "#2563EB",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
