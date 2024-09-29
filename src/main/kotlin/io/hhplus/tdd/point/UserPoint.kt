package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    var point: Long = 0,
    var updateMillis: Long = 0,
) {

    fun charge(amount: Long) {
        point += amount
    }

    fun use(amount: Long) {
        point -= amount
    }

}
