package io.tvelu77.freya.domain.services

import io.tvelu77.freya.domain.models.CycleEntry
import io.tvelu77.freya.domain.models.CycleEntryResult
import io.tvelu77.freya.domain.models.PhaseType
import io.tvelu77.freya.domain.models.UserProfile
import io.tvelu77.freya.domain.ports.spi.FakeCycleRepository
import io.tvelu77.freya.domain.ports.spi.FakeNotificationScheduler
import io.tvelu77.freya.domain.ports.spi.FakeUserProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TrackCycleValidationTest {

  private lateinit var repository: FakeCycleRepository
  private lateinit var profileRepository: FakeUserProfileRepository
  private lateinit var scheduler: FakeNotificationScheduler
  private lateinit var useCase: TrackCycleUseCaseAdapter

  @Before
  fun setup() {
    repository = FakeCycleRepository()
    profileRepository = FakeUserProfileRepository()
    scheduler = FakeNotificationScheduler()
    useCase = TrackCycleUseCaseAdapter(
      repository,
      PhaseCalculatorAdapter(),
      profileRepository,
      PhaseNotificationPlannerAdapter(),
      scheduler
    )
  }

  @Test
  fun `startCycle succeeds and saves when there is no overlap`() = runTest {
    val result = useCase.startCycle(LocalDate.of(2024, 3, 1))

    assertEquals(CycleEntryResult.Success(1L), result)
    assertEquals(1, repository.cycles.size)
  }

  @Test
  fun `startCycle returns error when the date overlaps a recorded period`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    val result = useCase.startCycle(LocalDate.of(2024, 3, 3))

    assertTrue(result is CycleEntryResult.Error)
    assertEquals(1, repository.cycles.size)
  }

  @Test
  fun `startCycle closes an ongoing cycle when starting inside it`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val result = useCase.startCycle(LocalDate.of(2024, 3, 3))

    assertTrue(result is CycleEntryResult.Success)
    assertEquals(LocalDate.of(2024, 3, 2), repository.cycles.first { it.id == 1L }.endDate)
  }

  @Test
  fun `endPeriod returns error when the end date is before the start date`() = runTest {
    val cycle = CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 10), endDate = null, cycleLengthDays = 28)

    val result = useCase.endPeriod(cycle, LocalDate.of(2024, 3, 5))

    assertTrue(result is CycleEntryResult.Error)
  }

  @Test
  fun `endPeriod saves the end date and computes the length from the previous cycle`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 2, 1), endDate = LocalDate.of(2024, 2, 5), cycleLengthDays = 28)
    )
    repository.cycles.add(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val result = useCase.endPeriod(repository.cycles.first { it.id == 2L }, LocalDate.of(2024, 3, 6))

    assertTrue(result is CycleEntryResult.Success)
    val updated = repository.cycles.first { it.id == 2L }
    assertEquals(LocalDate.of(2024, 3, 6), updated.endDate)
    assertEquals(29, updated.cycleLengthDays)
  }

  @Test
  fun `updateCycle returns error when the end date precedes the start date`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    val result = useCase.updateCycle(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 10), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    assertTrue(result is CycleEntryResult.Error)
  }

  @Test
  fun `updateCycle returns error when it overlaps another recorded period`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 2, 1), endDate = LocalDate.of(2024, 2, 6), cycleLengthDays = 28)
    )
    repository.cycles.add(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val result = useCase.updateCycle(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 2, 3), endDate = null, cycleLengthDays = 28)
    )

    assertTrue(result is CycleEntryResult.Error)
  }

  @Test
  fun `updateCycle saves a valid edit`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 1), endDate = LocalDate.of(2024, 3, 5), cycleLengthDays = 28)
    )

    val result = useCase.updateCycle(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 3, 2), endDate = LocalDate.of(2024, 3, 6), cycleLengthDays = 28)
    )

    assertEquals(CycleEntryResult.Success(1L), result)
    assertEquals(LocalDate.of(2024, 3, 2), repository.cycles.single().startDate)
    assertEquals(LocalDate.of(2024, 3, 6), repository.cycles.single().endDate)
  }

  @Test
  fun `deleteCycle removes the cycle`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 7, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    useCase.deleteCycle(7L)

    assertTrue(repository.cycles.isEmpty())
  }

  @Test
  fun `getCurrentPhase wraps the day count with the personalized length`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 1, 1), endDate = null, cycleLengthDays = 35)
    )

    val phase = useCase.getCurrentPhase(LocalDate.of(2024, 2, 5)).first()

    assertEquals(1, phase?.daysInCycle)
    assertEquals(PhaseType.MENSTRUAL, phase?.phase)
  }

  @Test
  fun `getNextCyclePredictions uses the average length of past cycles`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 1, 5), cycleLengthDays = 28)
    )
    repository.cycles.add(
      CycleEntry(id = 2, startDate = LocalDate.of(2024, 1, 30), endDate = LocalDate.of(2024, 2, 3), cycleLengthDays = 28)
    )
    repository.cycles.add(
      CycleEntry(id = 3, startDate = LocalDate.of(2024, 3, 1), endDate = null, cycleLengthDays = 28)
    )

    val predictions = useCase.getNextCyclePredictions(2).first()

    assertEquals(LocalDate.of(2024, 3, 31), predictions[0])
    assertEquals(LocalDate.of(2024, 4, 30), predictions[1])
  }

  @Test
  fun `reschedule schedules notifications when they are enabled`() = runTest {
    repository.cycles.add(
      CycleEntry(id = 1, startDate = LocalDate.now().minusDays(2), endDate = null, cycleLengthDays = 28)
    )

    useCase.rescheduleNotifications()

    assertEquals(1, scheduler.scheduled.size)
    assertTrue(scheduler.scheduled[0].isNotEmpty())
  }

  @Test
  fun `reschedule cancels notifications when disabled in the profile`() = runTest {
    profileRepository.profile = UserProfile(notificationsEnabled = false)

    useCase.rescheduleNotifications()

    assertEquals(1, scheduler.cancelCount)
    assertTrue(scheduler.scheduled.isEmpty())
  }

  @Test
  fun `reschedule cancels notifications when there is no cycle`() = runTest {
    useCase.rescheduleNotifications()

    assertEquals(1, scheduler.cancelCount)
    assertTrue(scheduler.scheduled.isEmpty())
  }
}
