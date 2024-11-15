package com.edison.lakbayuplb.algorithm.notifications
import android.content.Context
import android.content.Intent
import com.edison.lakbayuplb.data.building.BuildingRepository
import com.edison.lakbayuplb.data.classes.ExamScheduleRepository
import kotlinx.coroutines.flow.firstOrNull
import org.osmdroid.util.GeoPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

const val ACTION_SHOW_NOTIFICATION = "com.edison.lakbayuplb.SHOW_NOTIFICATION"


fun startAlarmService(context: Context) {
    val serviceIntent = Intent(context, AlarmForegroundService::class.java)
    context.startForegroundService(serviceIntent)
}
fun stopAlarmService(context: Context) {
    val serviceIntent = Intent(context, AlarmForegroundService::class.java)
    context.stopService(serviceIntent)
}

suspend fun getRoomCoordinates(roomId: Int, buildingRepository: BuildingRepository): GeoPoint {
    val room = buildingRepository.getRoom(roomId).firstOrNull()
    return GeoPoint(room!!.latitude,room.longitude)
}

fun getNextClassDateTime(classDays: String, classTime: LocalTime): LocalDateTime? {
    val today = LocalDateTime.now()
    val dayMap = mapOf(
        "M" to DayOfWeek.MONDAY,
        "T" to DayOfWeek.TUESDAY,
        "W" to DayOfWeek.WEDNESDAY,
        "TH" to DayOfWeek.THURSDAY,
        "F" to DayOfWeek.FRIDAY,
        "S" to DayOfWeek.SATURDAY
    )
    val daysList = mutableListOf<DayOfWeek>()
    var i = 0
    while (i < classDays.length) {
        if (i + 1 < classDays.length && classDays.substring(i, i + 2) == "TH") {
            daysList.add(dayMap["TH"]!!)
            i += 2
        } else {
            dayMap[classDays.substring(i, i + 1)]?.let { daysList.add(it) }
            i += 1
        }
    }

    var nextClassDate: LocalDateTime? = null

    for (classDay in daysList) {
        var potentialDate = today.with(classDay).withHour(classTime.hour).withMinute(classTime.minute).withSecond(0).withNano(0)
        if (potentialDate.isBefore(today) || (potentialDate.dayOfWeek == today.dayOfWeek && potentialDate.toLocalTime().isBefore(today.toLocalTime()))) {
            potentialDate = potentialDate.plusWeeks(1)
        }

        if (nextClassDate == null || potentialDate.isBefore(nextClassDate)) {
            nextClassDate = potentialDate
        }
    }
    return nextClassDate
}


suspend fun getWeeklyExamSummary(context: Context, examRepository: ExamScheduleRepository) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val endOfWeek = today.with(DayOfWeek.SUNDAY)

    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    val examsThisWeek = examRepository.getAllExamsSchedules()
        .firstOrNull()
        ?.filter { exam ->
            try {
                val examDate = LocalDate.parse(exam.date, formatter)
                examDate in startOfWeek..endOfWeek
            } catch (e: DateTimeParseException) {
                false
            }
        } ?: emptyList()

    if (examsThisWeek.isNotEmpty()) {
        val examCount = examsThisWeek.size
        val message =
            "You have $examCount ${if (examCount > 1) "Exams" else "Exam"} this Week! Better study hard!"
        sendWeeklyNotification(context, message)
    }
}
