package org.example

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PeakDetector {
    companion object {
        fun findPeaks(
            signal: DoubleArray,
            prominence: Double = 1.0,
            distance: Int = 1
        ): Pair<List<Int>, List<Int>> {
            val positivePeaks = findSignalPeaks(signal, prominence, distance)
            val negativePeaks = findSignalPeaks(signal.map { -it }.toDoubleArray(), prominence, distance)

            return Pair(positivePeaks, negativePeaks)
        }

        private fun findSignalPeaks(
            signal: DoubleArray,
            prominence: Double,
            distance: Int
        ): List<Int> {
            val peaks = mutableListOf<Int>()

            // Find local maxima
            for (i in 1 until signal.size - 1) {
                if (signal[i] > signal[i - 1] && signal[i] > signal[i + 1]) {
                    peaks.add(i)
                }
            }

            // Filter peaks by prominence
            return peaks.filter { peak ->
                calculateProminence(signal, peak) >= prominence
            }.filterByDistance(distance)
        }

        private fun calculateProminence(signal: DoubleArray, peakIndex: Int): Double {
            val peakValue = signal[peakIndex]

            // Look left for lower bound
            var leftMin = peakValue
            for (i in peakIndex downTo 0) {
                leftMin = min(leftMin, signal[i])
                if (signal[i] > peakValue) break
            }

            // Look right for lower bound
            var rightMin = peakValue
            for (i in peakIndex until signal.size) {
                rightMin = min(rightMin, signal[i])
                if (signal[i] > peakValue) break
            }

            // Prominence is the minimum height difference
            return peakValue - max(leftMin, rightMin)
        }

        private fun List<Int>.filterByDistance(minDistance: Int): List<Int> {
            if (isEmpty()) return emptyList()

            val filtered = mutableListOf<Int>()
            var lastPeak = this[0]
            filtered.add(lastPeak)

            for (peak in this) {
                if (peak - lastPeak >= minDistance) {
                    filtered.add(peak)
                    lastPeak = peak
                }
            }

            return filtered
        }
    }
}
