package jp.co.nintendo.chat.data.repository.impl.time

import javax.inject.Inject

/**
 * A time calculator to get UNIX milliseconds in current system using [System] class
 */
class SystemCurrentMillisCalculator @Inject constructor() {
    fun getCurrentMillis(): Long = System.currentTimeMillis()
}
