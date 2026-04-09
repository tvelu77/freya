package io.tvelu77.freya.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tvelu77.freya.data.PeriodRepository
import io.tvelu77.freya.models.Period
import io.tvelu77.freya.models.Phase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CycleViewModel @Inject constructor(
    private val repository: PeriodRepository
) : ViewModel() {

    val periods = repository.allPeriods.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lastPeriod = repository.lastPeriod.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val averageCycleLength: StateFlow<Int> = periods.map { list ->
        calculateAverageCycleLength(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 28)

    val history: StateFlow<List<CycleEntry>> = periods.map { list ->
        list.mapIndexed { index, period ->
            val previousPeriod = list.getOrNull(index + 1)

            CycleEntry(
                startDate = period.startDate,
                periodDuration = ChronoUnit.DAYS.between(period.startDate, period.endDate).toInt() + 1,
                cycleLength = previousPeriod?.let { prev ->
                    ChronoUnit.DAYS.between(prev.startDate, period.startDate).toInt()
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nextPeriod: StateFlow<LocalDate?> = combine(lastPeriod, averageCycleLength) { last, avg ->
        last?.startDate?.plusDays(avg.toLong())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentPhase: StateFlow<Phase?> = combine(lastPeriod, averageCycleLength) { last, avg ->
        calculatePhase(last, avg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun calculateAverageCycleLength(list: List<Period>): Int {
        if (list.size < 2) return 28
        // Calcul entre les dates de début successives
        val lengths = list.zipWithNext { a, b ->
            ChronoUnit.DAYS.between(b.startDate, a.startDate).toInt()
        }
        return lengths.average().toInt().coerceIn(21, 45)
    }

    private fun calculatePhase(last: Period?, avgLength: Int): Phase? {
        if (last == null) return null
        val today = LocalDate.now()
        if (today.isBefore(last.startDate)) return null

        val cycleDay = ChronoUnit.DAYS.between(last.startDate, today).toInt() + 1
        return when {
            !today.isAfter(last.endDate) -> Phase.MENSTRUELLE
            cycleDay <= 5 -> Phase.MENSTRUELLE
            else -> {
                val ovulationWindowStart = avgLength - 16
                val ovulationWindowEnd = avgLength - 12
                when {
                    cycleDay in ovulationWindowStart..ovulationWindowEnd -> Phase.OVULATOIRE
                    cycleDay < ovulationWindowStart -> Phase.FOLLICULAIRE
                    else -> Phase.LUTEALE
                }
            }
        }
    }

    fun addNewPeriod(start: LocalDate, end: LocalDate, notes: String? = null) {
        viewModelScope.launch {
            repository.addPeriod(start, end, notes)
        }
    }
}

data class CycleEntry(
    val startDate: LocalDate,
    val periodDuration: Int,
    val cycleLength: Int?
)
