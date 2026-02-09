package io.tvelu77.freya.sqlite

import io.tvelu77.freya.domain.models.Cycle
import io.tvelu77.freya.domain.ports.spi.CycleRepository
import io.tvelu77.freya.sqlite.dao.CycleDao
import io.tvelu77.freya.sqlite.models.toEntity

class CycleRepositoryAdapter(private val cycleDao: CycleDao) : CycleRepository {

    override fun save(cycle: Cycle) {
        cycleDao.save(cycle.toEntity())
    }

    override fun findAll(): List<Cycle> {
        val entities = cycleDao.findAll();
        TODO("Not yet implemented")
    }

}