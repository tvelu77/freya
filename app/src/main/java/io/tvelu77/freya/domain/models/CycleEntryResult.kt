package io.tvelu77.freya.domain.models

sealed interface CycleEntryResult {

  data class Success(val id: Long) : CycleEntryResult

  data class Error(val message: String) : CycleEntryResult
}
