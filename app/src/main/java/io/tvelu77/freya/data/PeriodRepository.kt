package io.tvelu77.freya.data

import io.tvelu77.freya.models.Period
import jakarta.inject.Inject
import java.time.LocalDate

class PeriodRepository @Inject constructor(private val dao: PeriodDao) {

    val allPeriods = dao.findAll()
    val lastPeriod = dao.findLast()

    suspend fun addPeriod(start: LocalDate, end: LocalDate, notes: String? = null) {
        dao.insert(Period(startDate = start, endDate = end, notes = notes))
    }

}