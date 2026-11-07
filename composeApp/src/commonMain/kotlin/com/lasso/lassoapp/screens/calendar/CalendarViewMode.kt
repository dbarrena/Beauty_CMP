package com.lasso.lassoapp.screens.calendar

enum class CalendarViewMode(val dayCount: Int) {
    Day(1),
    ThreeDays(3),
}

enum class CalendarRangeDirection(private val sign: Int) {
    Previous(-1),
    Next(1);

    fun dayOffset(dayCount: Int): Int = sign * dayCount
}
