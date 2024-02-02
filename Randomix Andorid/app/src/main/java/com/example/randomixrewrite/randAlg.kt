package com.example.randomixrewrite

import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class randAlg(private val responseBody: WeatherData) {
    fun shuffleBits(mask: ULong): List<Boolean> {
        val bitsArray = mutableListOf<Boolean>()


        for (i in 0 until 64) {
            val bit = mask and (1UL shl i) != 0UL
            bitsArray.add(bit)
        }


        bitsArray.shuffle()

        return bitsArray
    }

    fun createNumberFromMask(range: IntRange): Int {
        require(range.first <= range.last) { "Lower bound of the range must be less than or equal to the upper bound." }

        val mask = createMaskFromNumber()
        val shuffledBits = shuffleBits(mask)


        val requiredBitCount = min(64, ceil(log2((range.last - range.first + 1).toDouble())).toInt())


        var result = 0UL
        for ((index, bit) in shuffledBits.take(requiredBitCount).withIndex()) {
            if (bit) {
                result = result or (1UL shl index)
            }
        }


        result = result and ((1UL shl requiredBitCount) - 1UL)


        val finalResult = max(range.first, min(range.last, result.toInt()))

        return finalResult
    }

    private fun createMaskFromNumber(): ULong {
        val tempMax = responseBody.main?.tempMax?.toInt() ?: 0
        val tempMin = responseBody.main?.tempMin?.toInt() ?: 0
        val temp = responseBody.main?.temp?.toInt() ?: 0
        val humidity = responseBody.main?.humidity?.toInt() ?: 0
        val pressure = responseBody.main?.pressure?.toInt() ?: 0

        var mask = (tempMax.toULong() shl 10) +
                (tempMin.toULong() shl 20) -
                (temp.toULong() shl 30) +
                (humidity.toULong() shl 40) -
                (pressure.toULong() shl 50)

        mask = (mask shl 7) or (mask shr 57)

        return mask
    }
}