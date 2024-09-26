package io.hhplus.tdd

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointRepositoryImpl
import io.hhplus.tdd.point.PointService
import io.hhplus.tdd.point.PointValidator
import io.hhplus.tdd.point.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

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
    fun `최초 유저 포인트 조회 시 0 반환`() {
        val userPoint = service.get(1)

        assertEquals(userPoint.point, 0)
    }

    @Test
    fun `포인트 이력 조회`() {
        service.charge(1, 2000)
        service.use(1, 1000)

        val histories = service.getHistory(1)
        val case1 = histories[0]
        val case2 = histories[1]

        assertEquals(histories.size, 2)

        assertEquals(case1.userId, 1)
        assertEquals(case1.amount, 2000)
        assertEquals(case1.type, TransactionType.CHARGE)

        assertEquals(case2.userId, 1)
        assertEquals(case2.amount, 1000)
        assertEquals(case2.type, TransactionType.USE)

    }

    @Test
    fun `포인트 충전`() {
        service.charge(1, 3000)

        val userPoint = service.get(1)

        assertEquals(userPoint.point, 3000)
    }


    @Test
    fun `포인트 사용`() {
        service.charge(1, 10000)
        service.use(1, 5000)

        val userPoint = service.get(1)

        assertEquals(userPoint.point, 5000)
    }


}