package com.rodrigmatrix.sippa.Serializer

data class Class(
    var id: String,
    var name: String,
    var professor: String,
    var professorEmail: String,
    var credits: Int,
    var period: String,
    var code: String,
    var Grades: Grades,
    var news: List<News>,
    var percentageAttendance: String,
    var totalAttendance: Int,
    var missed: Int
)