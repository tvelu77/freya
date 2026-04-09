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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    private val _currentPhase = MutableStateFlow<Phase?>(null)
    val currentPhase: StateFlow<Phase?> = _currentPhase

    private val _averageCycleLength = MutableStateFlow(28)
    val averageCycleLength: StateFlow<Int> = _averageCycleLength

    init {
        viewModelScope.launch {
            periods.collect { list ->
                _averageCycleLength.value = calculateAverageCycleLength(list)
                updateCurrentPhase()
            }
        }
    }

    private fun calculateAverageCycleLength(list: List<Period>): Int {
        if (list.size < 2) return 28
        val lengths = list.zipWithNext { a, b ->
            ChronoUnit.DAYS.between(a.startDate, b.startDate).toInt()
        }
        return lengths.average().toInt().coerceIn(21, 45) // variations individuelles réalistes
    }

    fun addNewPeriod(start: LocalDate, end: LocalDate, notes: String? = null) {
        viewModelScope.launch {
            repository.addPeriod(start, end, notes)
        }
    }

    private fun updateCurrentPhase() {
        val last = lastPeriod.value ?: return
        val today = LocalDate.now()
        if (today.isBefore(last.startDate)) return

        val cycleDay = ChronoUnit.DAYS.between(last.startDate, today).toInt() + 1
        val avgLength = _averageCycleLength.value

        _currentPhase.value = when {
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

    fun predictNextPeriod(): LocalDate? {
        val last = lastPeriod.value ?: return null
        return last.startDate.plusDays(averageCycleLength.value.toLong())
    }

    fun getCycleHistory(): List<CycleEntry> {
        return periods.value.zipWithNext { prev, next ->
            CycleEntry(
                startDate = prev.startDate,
                periodDuration = ChronoUnit.DAYS.between(prev.startDate, prev.endDate).toInt() + 1,
                cycleLength = ChronoUnit.DAYS.between(prev.startDate, next.startDate).toInt()
            )
        }
    }
}

data class CycleEntry(
    val startDate: LocalDate,
    val periodDuration: Int,
    val cycleLength: Int
)