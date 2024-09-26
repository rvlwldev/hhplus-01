package io.hhplus.tdd

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointRepositoryImpl
import io.hhplus.tdd.point.PointService
import io.hhplus.tdd.point.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CompletableFuture

@SpringBootTest
class PointServiceConcurrencyTest {
    @Mock
    private lateinit var pointTable: UserPointTable

    @Mock
    private lateinit var historyTable: PointHistoryTable

    @InjectMocks
    private lateinit var repository: PointRepositoryImpl

    private lateinit var service: PointService

    @BeforeEach
    fun setUp() {
        pointTable = UserPointTable()
        historyTable = PointHistoryTable()
        repository = PointRepositoryImpl(pointTable, historyTable)
        service = PointService(repository)
    }

    @Test
    fun `같은 ID에 대해서, 동시에 여러 요청이 들어왔을때 각 처리 시간이 상이하더라도 순서가 보장되어야 한다`() {
        val requestCount = 100

        val futures = (1..requestCount).map { i ->
            CompletableFuture.runAsync {
                if (i % 2 == 1) service.charge(1L, 100L)
                else service.use(1L, 100L)
            }
        }

        CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            val historyList = service.getHistory(1L)
            (1..requestCount)
                .forEach {
                    val type = historyList[it - 1].type
                    if (it % 2 == 1) assertEquals(type, TransactionType.CHARGE, "순서가 보장되지 않음")
                    else assertEquals(type, TransactionType.USE, "순서가 보장되지 않음")
                }
        }

    }

    @Test
    fun `다른 ID에 대해서, 락에 대한 순서 영향을 받지 않아야 한다`() {
        val requestCount = 100
        val futures = (1..requestCount).flatMap {
            listOf(
                CompletableFuture.runAsync { service.charge(1L, 100L) },
                CompletableFuture.runAsync { service.charge(2L, 100L) }
            )
        }

        CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            val targetList = service.getHistory(1L)

            val timeList = service.getHistory(1L).map { it.timeMillis }
            val min = timeList.min()
            val max = timeList.max()

            var result = false
            for (i: Int in targetList.indices)
                if (targetList[i].timeMillis in (min + 1)..<max) {
                    result = true
                    break
                }

            assertTrue(result)
        }

    }
}