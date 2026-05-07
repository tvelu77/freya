package io.tvelu77.freya.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
  @TypeConverter
  fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

  @TypeConverter
  fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}