package com.example.classschedule.algorithm

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    } catch (e: Exception) {
        null
    }
}