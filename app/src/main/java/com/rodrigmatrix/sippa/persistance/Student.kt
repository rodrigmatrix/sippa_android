package com.rodrigmatrix.sippa.persistance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    var id: Int,
    var jsession: String,
    var responseHtml: String
)