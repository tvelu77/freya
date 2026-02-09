package io.tvelu77.freya.domain.ports.spi

import io.tvelu77.freya.domain.models.Cycle

interface CycleRepository {

    fun save(cycle: Cycle)
    fun findAll(): List<Cycle>

}