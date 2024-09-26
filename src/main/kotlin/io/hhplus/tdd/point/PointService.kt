package io.hhplus.tdd.point

import org.springframework.stereotype.Service


@Service
class PointService(
    private val validator: PointValidator,
    private val repo: PointRepositoryImpl
) {

    fun get(id: Long): UserPoint = repo.get(id)

    fun getHistory(id: Long): List<PointHistory> = repo.getHistories(id)

    fun charge(id: Long, amount: Long): UserPoint {
        val userPoint = repo.get(id)
        validator.validate(userPoint, amount, TransactionType.CHARGE)
        userPoint.charge(amount)

        return repo.save(userPoint, amount, TransactionType.CHARGE)
    }

    fun use(id: Long, amount: Long): UserPoint {
        val userPoint = repo.get(id)
        validator.validate(userPoint, amount, TransactionType.USE)
        userPoint.use(amount)

        return repo.save(userPoint, amount, TransactionType.USE)
    }

}