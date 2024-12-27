package org.example

class CenterOfPressure(
    data: Array<Array<DoubleArray>>,
    positions: Map<String, Map<String, DoubleArray>>? = null
) {
    private val leftData: Array<DoubleArray> = data[0]
    private val rightData: Array<DoubleArray> = data[1]
    private val positions: Map<String, Map<String, DoubleArray>> = positions ?: defaultPositions()

    private companion object {
        const val EPSILON = 1e-10
    }

    fun getData(side: String): Array<DoubleArray> =
        when (side) {
            "left" -> leftData
            "right" -> rightData
            else -> throw IllegalArgumentException("Invalid side")
        }

    fun getCopFootTrajectory(side: String): List<Pair<Double, Double>> {
        require(side in listOf("left", "right")) { "Invalid side. Must be 'left' or 'right'." }

        val data = if (side == "left") leftData else rightData
        val positionsSide = positions[side] ?: error("No positions found for $side")

        return data.map { row ->
            val sum = row.sum()
            val copX = row.mapIndexed { index, value ->
                value * (positionsSide["x"]?.get(index) ?: 0.0)
            }.sum() / (sum + EPSILON)
            val copY = row.mapIndexed { index, value ->
                value * (positionsSide["y"]?.get(index) ?: 0.0)
            }.sum() / (sum + EPSILON)
            Pair(copX, copY)
        }
    }

    private fun defaultPositions(): Map<String, Map<String, DoubleArray>> = mapOf(
        "left" to mapOf(
            "x" to doubleArrayOf(-15.781, 0.151, 16.421, 16.257, 32.360, 9.439, 25.545,
                3.566, -16.666, -15.802, -31.683, -32.360, -24.562, -21.915),
            "y" to doubleArrayOf(-72.290, -103.172, -72.955, -13.299, 60.864, 63.219,
                92.967, 103.172, 89.974, 54.246, 71.145, 33.512, 8.389, -18.231)
        ),
        "right" to mapOf(
            "x" to doubleArrayOf(15.781, -0.151, -16.421, -16.257, -32.360, -9.439,
                -25.545, -3.566, 16.666, 15.802, 31.683, 32.360, 24.562, 21.915),
            "y" to doubleArrayOf(-72.290, -103.172, -72.955, -13.299, 60.864, 63.219,
                92.967, 103.172, 89.974, 54.246, 71.145, 33.512, 8.389, -18.231)
        )
    )
}
