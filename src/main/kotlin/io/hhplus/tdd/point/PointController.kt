package io.hhplus.tdd.point

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(private val service: PointService) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("{id}")
    fun point(@PathVariable id: Long): UserPoint {
        val userPoint = service.get(id)
        logger.info("GET: $userPoint")
        return userPoint;
    }

    @GetMapping("{id}/histories")
    fun history(@PathVariable id: Long): List<PointHistory> {
        return service.getHistory(id)
    }

    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        val userPoint = service.charge(id, amount)
        logger.info("CHARGE: $userPoint")
        return userPoint
    }

    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        val userPoint = service.use(id, amount)
        logger.info("USE: $userPoint")
        return userPoint
    }
}