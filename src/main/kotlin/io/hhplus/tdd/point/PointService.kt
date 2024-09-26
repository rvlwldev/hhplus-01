package io.hhplus.tdd.point

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


@Service
class PointService(
    private val repo: PointRepositoryImpl
) {
    private val validator = PointValidator()
    private val lockerMap = ConcurrentHashMap<Long, ReentrantLock>()

    fun get(id: Long): UserPoint = repo.get(id)

    fun getHistory(id: Long): List<PointHistory> = repo.getHistories(id)

    fun charge(id: Long, amount: Long): UserPoint {
        val locker = lockerMap.computeIfAbsent(id) { ReentrantLock() }
        locker.withLock {
            val userPoint = repo.get(id)
            validator.validate(userPoint, amount, TransactionType.CHARGE)
            userPoint.charge(amount)

            return repo.save(userPoint, amount, TransactionType.CHARGE)
        }
    }

    fun use(id: Long, amount: Long): UserPoint {
        val locker = lockerMap.computeIfAbsent(id) { ReentrantLock() }
        locker.withLock {
            val userPoint = repo.get(id)
            validator.validate(userPoint, amount, TransactionType.USE)
            userPoint.use(amount)

            return repo.save(userPoint, amount, TransactionType.USE)
        }
    }

}