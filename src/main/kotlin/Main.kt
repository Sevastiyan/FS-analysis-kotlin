package org.example


import org.example.CenterOfPressure
import org.example.ButterLowpassFilter
import org.example.PeakDetector
import kotlin.math.cos
import kotlin.math.sin
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomLine
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.label.ggtitle



fun main() {
    // Sample data setup (14 sensors, 100 samples)
    val sampleCount = 100
    val sensorCount = 14

    /* ---------------------------- Create Dummy Data --------------------------- */
    // Create sample data for left and right feet
    val leftData = Array(sampleCount) { i ->
        DoubleArray(sensorCount) { j ->
            // Simulate pressure data with some noise
            100.0 + sin(i * 0.1) * 10 + Math.random() * 5
        }
    }

    val rightData = Array(sampleCount) { i ->
        DoubleArray(sensorCount) { j ->
            // Simulate pressure data with some noise
            100.0 + cos(i * 0.1) * 10 + Math.random() * 5
        }
    }

    /* ---------------------------- Filter parameters --------------------------- */
    val cutoff = 5.0  // Hz
    val samplingFrequency = 100.0  // Hz
    val filterOrder = 2

    /* ------------------- Apply filter to each sensor column ------------------- */
    val filteredLeft = Array(sampleCount) { DoubleArray(sensorCount) }
    val filteredRight = Array(sampleCount) { DoubleArray(sensorCount) }

    for (sensorIndex in 0 until sensorCount) {
        // Extract single sensor data
        val leftSensorData = DoubleArray(sampleCount) { i -> leftData[i][sensorIndex] }
        val rightSensorData = DoubleArray(sampleCount) { i -> rightData[i][sensorIndex] }

        // Apply filter
        val filteredLeftSensor = ButterLowpassFilter.filter(
            leftSensorData,
            cutoff,
            samplingFrequency,
            filterOrder
        )
        val filteredRightSensor = ButterLowpassFilter.filter(
            rightSensorData,
            cutoff,
            samplingFrequency,
            filterOrder
        )

        // Store filtered results
        for (i in 0 until sampleCount) {
            filteredLeft[i][sensorIndex] = filteredLeftSensor[i]
            filteredRight[i][sensorIndex] = filteredRightSensor[i]
        }
    }

    // Create data array for CenterOfPressure
    val copData = arrayOf(filteredLeft, filteredRight)


    /* -------------------------------------------------------------------------- */
    /*                                Analysis COP                                */
    /* -------------------------------------------------------------------------- */

    // Process with CenterOfPressure
    val cop = CenterOfPressure(copData)

    val leftTrajectory = cop.getCopFootTrajectory("left")
    val rightTrajectory = cop.getCopFootTrajectory("right")

    // Extract Y coordinates from left trajectory
    val leftCopY = leftTrajectory.map { it.second }.toDoubleArray()

    println("Y-coordinates of left foot trajectory:")
    leftCopY.forEachIndexed { index, value ->
        println("Sample $index: ${"%.2f".format(value)}")
    }


    /* ----------------------------- Filter COP data ---------------------------- */
//    val filteredLeftY = ButterLowpassFilter.filter(
//        leftCopY, // We use only Y axis data
//        cutoff = 2.5,
//        fs = 100.0,
//        order = 2
//    )

    /* -------------------------------------------------------------------------- */
    /*                                 Find Peaks                                 */
    /* -------------------------------------------------------------------------- */
    val (positivePeaks, negativePeaks) = PeakDetector.findPeaks(
        leftCopY,
        prominence = 0.1, // Adjust based on your signal
        distance = 1     // Minimum samples between peaks
    )


    /* ---------------------------------- Plot ---------------------------------- */
    val data = mapOf(
        "sample" to (0 until sampleCount).toList(),
        "yCoord" to leftCopY.toList()
    )

    val positivePeakData = mapOf(
        "sample" to positivePeaks,
        "yCoord" to positivePeaks.map { leftCopY[it] }
    )

    val negativePeakData = mapOf(
        "sample" to negativePeaks,
        "yCoord" to negativePeaks.map { leftCopY[it] }
    )

    val plot = letsPlot(data) +
        geomLine(color = "blue") { x = "sample"; y = "yCoord" } +
        geomPoint(data = positivePeakData, color = "green", size = 3.0) { x = "sample"; y = "yCoord" } +
        geomPoint(data = negativePeakData, color = "red", size = 3.0) { x = "sample"; y = "yCoord" } +
        ggtitle("Left Foot COP Y-Trajectory")

    // Save plot to file
    ggsave(plot, "cop_plot.png")
    /* ------------------------------------ x ----------------------------------- */

    println("these are the positive peaks, $positivePeaks")

    positivePeaks.forEachIndexed { index, value ->
        println("Positive peak $index at sample $value: ${"%.2f".format(leftCopY[value])}")
    }

    println("these are the negative peaks, $negativePeaks")
    negativePeaks.forEachIndexed {inde, value ->
        println("Negative peak $inde at sample $value: ${"%.2f".format(leftCopY[value])}")
    }

}
