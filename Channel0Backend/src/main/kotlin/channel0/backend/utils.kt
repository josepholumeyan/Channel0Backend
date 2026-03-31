package channel0Backend.channel0.backend

inline fun <T> logTiming(actionName: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    println("$actionName took ${end - start} ms") // or use logger.info(...)
    return result
}
