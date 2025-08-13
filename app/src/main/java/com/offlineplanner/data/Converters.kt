package com.offlineplanner.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(v: LocalDate?): String? = v?.toString()

    @TypeConverter
    fun toLocalDate(v: String?): LocalDate? = v?.let(LocalDate::parse)

    @TypeConverter
    fun fromLocalTime(v: LocalTime?): String? = v?.toString()

    @TypeConverter
    fun toLocalTime(v: String?): LocalTime? = v?.let(LocalTime::parse)

    @TypeConverter
    fun fromRecurrenceType(v: RecurrenceType?): String? = v?.name

    @TypeConverter
    fun toRecurrenceType(v: String?): RecurrenceType = v?.let { RecurrenceType.valueOf(it) } ?: RecurrenceType.NONE

    @TypeConverter
    fun fromAlarmKind(v: AlarmKind?): String? = v?.name

    @TypeConverter
    fun toAlarmKind(v: String?): AlarmKind = v?.let { AlarmKind.valueOf(it) } ?: AlarmKind.SYSTEM_CLOCK
}