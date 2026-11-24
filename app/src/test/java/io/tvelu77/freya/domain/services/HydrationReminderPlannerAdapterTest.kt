package io.tvelu77.freya.domain.services

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class HydrationReminderPlannerAdapterTest {

  private val planner = HydrationReminderPlannerAdapter()

  @Test
  fun `plan returns at most two reminders`() {
    val now = LocalDateTime.of(2024, 6, 10, 8, 0)

    val reminders = planner.plan(now)

    assertEquals(2, reminders.size)
    assertTrue(reminders.all { !it.triggerAt.isBefore(now) })
    assertTrue(reminders.all { it.triggerAt.toLocalDate() == now.toLocalDate() })
  }

  @Test
  fun `only future reminders of the day are kept`() {
    val noon = LocalDateTime.of(2024, 6, 10, 12, 0)
    val evening = LocalDateTime.of(2024, 6, 10, 18, 30)

    assertEquals(1, planner.plan(noon).size)
    assertEquals(0, planner.plan(evening).size)
  }

  @Test
  fun `reminders are gentle and mention water`() {
    val now = LocalDateTime.of(2024, 6, 10, 8, 0)

    planner.plan(now).forEach { reminder ->
      assertTrue(reminder.title.isNotBlank())
      assertTrue(reminder.message.contains("verre d'eau"))
    }
  }
}
