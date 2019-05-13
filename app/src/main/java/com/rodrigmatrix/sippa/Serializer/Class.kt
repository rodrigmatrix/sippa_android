package com.rodrigmatrix.sippa.Serializer

data class Class(
    var id: String,
    var name: String,
    var professor: String,
    var professorEmail: String,
    var credits: Int,
    var period: String,
    var code: String,
    var grades: MutableList<Grade>,
    var news: MutableList<News>,
    var classPlan: MutableList<ClassPlan>,
    var percentageAttendance: String,
    var totalAttendance: Attendance
)