package com.lasso.lassoapp.screens.calendar

enum class CalendarViewMode(val dayCount: Int) {
    Day(1),
    ThreeDays(3),
}

enum class CalendarRangeDirection(val dayOffset: Int) {
    Previous(-3),
    Next(3),
}
