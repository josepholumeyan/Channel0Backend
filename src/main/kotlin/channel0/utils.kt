package channel0

import org.slf4j.Logger


inline fun <T> logTiming(actionName: String, log: Logger, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    log.debug("$actionName took ${end - start} ms") // or use logger.info(...)
    return result
}
