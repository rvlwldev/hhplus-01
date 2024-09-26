package io.hhplus.tdd.point

import org.springframework.stereotype.Component

@Component
class PointValidator {
    private final val unit: Long = 100L
    private final val maximumPoint: Long = 100000L

    fun validate(userPoint: UserPoint, amount: Long, transactionType: TransactionType) {
        when {
            amount < 1 -> {
                throw IllegalArgumentException("0 또는 음수는 입력할 수 없습니다.")
            }

            amount % unit != 0L -> {
                throw IllegalArgumentException("$unit 단위로 포인트 충전이 가능합니다.")
            }

            transactionType == TransactionType.CHARGE && userPoint.point + amount > maximumPoint -> {
                throw IllegalArgumentException("충전 가능한 최대포인트를 초과하였습니다.(충전 가능 최대 포인트: $maximumPoint)")
            }

            transactionType == TransactionType.USE && userPoint.point - amount < 0 -> {
                throw IllegalArgumentException("보유 포인트보다 더 많이 사용할 수 없습니다.")
            }
        }
    }
}