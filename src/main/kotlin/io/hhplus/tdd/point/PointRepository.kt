package io.hhplus.tdd.point

interface PointRepository {
    fun get(id: Long): UserPoint
    fun getHistories(id: Long): List<PointHistory>
    fun save(userPoint: UserPoint, amount:Long, type: TransactionType): UserPoint
}