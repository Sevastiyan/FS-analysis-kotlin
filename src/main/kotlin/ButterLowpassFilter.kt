package org.example

import kotlin.math.*

class ButterLowpassFilter {
    companion object {
        fun filter(data: DoubleArray, cutoff: Double, fs: Double, order: Int): DoubleArray {
            val nyquist = fs * 0.5
            val normalCutoff = cutoff / nyquist
            val (b, a) = calculateCoefficients(normalCutoff, order)
            return applyFilter(data, b, a)
        }

        private fun calculateCoefficients(cutoff: Double, order: Int): Pair<DoubleArray, DoubleArray> {
            val omega = tan(PI * cutoff / 2.0)
            val omegaSq = omega * omega

            when (order) {
                1 -> {
                    val b = doubleArrayOf(omega, omega)
                    val a = doubleArrayOf(1.0 + omega, omega - 1.0)
                    return Pair(b, a)
                }
                2 -> {
                    val alpha = sin(PI / 4.0)
                    val b = DoubleArray(3) { omegaSq }
                    val a = doubleArrayOf(
                        1.0 + 2.0 * alpha * omega + omegaSq,
                        2.0 * (omegaSq - 1.0),
                        1.0 - 2.0 * alpha * omega + omegaSq
                    )
                    return Pair(b, a)
                }
                else -> throw IllegalArgumentException("Only orders 1 and 2 are supported")
            }
        }

        private fun applyFilter(data: DoubleArray, b: DoubleArray, a: DoubleArray): DoubleArray {
            val result = DoubleArray(data.size)
            val order = a.size - 1

            // Initialize history
            val x = DoubleArray(order + 1)
            val y = DoubleArray(order + 1)

            // Apply filter
            for (i in data.indices) {
                // Shift x history
                for (j in order downTo 1) x[j] = x[j-1]
                x[0] = data[i]

                // Compute new y
                var output = 0.0
                for (j in 0..order) {
                    output += b[j] * x[j]
                    if (j > 0) output -= a[j] * y[j-1]
                }
                output /= a[0]

                // Shift y history
                for (j in order downTo 1) y[j] = y[j-1]
                y[0] = output

                result[i] = output
            }

            return result
        }
    }
}
