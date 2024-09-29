package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Component


@Component
class PointRepositoryImpl(
    private val pointTable: UserPointTable,
    private val historyTable: PointHistoryTable
) : PointRepository {

    override fun get(id: Long): UserPoint = pointTable.selectById(id)

    override fun getHistories(id: Long): List<PointHistory> = historyTable.selectAllByUserId(id)

    override fun save(userPoint: UserPoint, amount: Long, type: TransactionType): UserPoint {
        val result = pointTable.insertOrUpdate(userPoint.id, userPoint.point);
        historyTable.insert(result.id, amount, type, result.updateMillis)

        return result;
    }

}