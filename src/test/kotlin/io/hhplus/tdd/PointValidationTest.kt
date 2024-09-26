package io.hhplus.tdd

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointRepositoryImpl
import io.hhplus.tdd.point.PointService
import io.hhplus.tdd.point.PointValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock

class PointValidationTest {

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
        service = PointService(PointValidator(), repository)
    }

    @Test
    fun `올바르지 않은 단위로 저장 및 사용`() {
        assertDoesNotThrow {
            service.charge(1, 100)
            service.use(1, 100)
        }
        assertThrows<IllegalArgumentException> {
            service.charge(2, 99)
            service.use(2, 99)
        }
        assertThrows<IllegalArgumentException> {
            service.charge(3, 101)
            service.use(3, 101)
        }
    }

    @Test
    fun `0 또는 음수로 저장 및 사용`() {
        assertDoesNotThrow {
            service.charge(1, 100)
            service.use(1, 100)
        }
        assertThrows<IllegalArgumentException> {
            service.charge(2, 0)
            service.use(2, 0)
        }
        assertThrows<IllegalArgumentException> {
            service.charge(3, -1)
            service.use(3, -1)
        }
    }

    @Test
    fun `포인트 충전 시 최대보유량을 넘을 수 없음`() {
        assertDoesNotThrow {
            service.charge(1, 99900)
        }
        assertDoesNotThrow {
            service.charge(1, 100)
        }

        assertDoesNotThrow {
            service.charge(2, 100000)
        }
        assertThrows<IllegalArgumentException> {
            service.charge(2, 100)
        }
    }

    @Test
    fun `보유 포인트 보다 더 많이 사용할 수 없음`() {
        assertDoesNotThrow {
            service.charge(1, 100)
            service.use(1, 100)
        }

        assertDoesNotThrow {
            service.charge(2, 100)
            service.charge(2, 200)
        }
    }

}