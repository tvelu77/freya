package io.tvelu77.freya.domain.models

import java.time.LocalDate

data class Cycle(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val duration: Int
)
