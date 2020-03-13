package com.gvkorea.gvktune

import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
   fun main(){
     val a = ansiBands(24, 24)
        println(Arrays.toString(a))
    }



    fun ansiBands(N: Int, bands: Int): DoubleArray? {
        val fi = DoubleArray(bands)
        val b = 1.toDouble() / N
        for (j in 0 until bands) {
            val i = j - bands / 3
            if (N % 2 == 0) fi[j] =
                1000.0 * Math.pow(2.0, (i + 1) * b / 2.0) else fi[j] =
                1000.0 * Math.pow(2.0, i * b)
        }
        return fi
    }

    /**
     * Returns the band limit frequencies of the Octave-Band and Fractional-Octave-Band filter
     * frequencies according to the
     * [
 * ANSI Specification
](https://law.resource.org/pub/us/cfr/ibr/002/ansi.s1.11.2004.pdf) * .
     *
     * @param N the fractional octave band to compute, i.e. 3 for 1/3 octave bands
     * @param bands the number of filter bands to compute
     *
     * @return an array of length bands+1 containing the upper and lower band limit frequency of
     * each 1/N octave band
     */
    fun ansiBandLimits(N: Int, bands: Int): DoubleArray? {
        var bands = bands
        bands++
        val fi = DoubleArray(bands)
        val b = 1.toDouble() / N
        for (j in 0 until bands) {
            val i = j - bands / 3
            if (N % 2 == 0) fi[j] =
                710.0 * Math.pow(2.0, (i + 1) * b / 2.0) else fi[j] =
                710.0 * Math.pow(2.0, i * b)
        }
        return fi
    }

    /**
     * Find the maximum of an array
     *
     * @param data the array
     *
     * @return the maximum value in the array
     */
    fun max(data: FloatArray): Float {
        var max = -Float.MAX_VALUE
        for (value in data) {
            if (value > max) max = value
        }
        return max
    }

    /**
     * Find the maximum of an array
     *
     * @param data the array
     *
     * @return the maximum value in the array
     */
    fun max(data: DoubleArray): Double {
        var max = -Double.MAX_VALUE
        for (value in data) {
            if (value > max) max = value
        }
        return max
    }

    /**
     * Find the index location of the maximum value of an array
     *
     * @param data the array
     *
     * @return the index location of the maximum value in the array
     */
    fun maxLoc(data: FloatArray): Int {
        var max = -Float.MAX_VALUE
        var maxLoc = 0
        for (i in data.indices) {
            if (data[i] > max) {
                max = data[i]
                maxLoc = i
            }
        }
        return maxLoc
    }

    /**
     * Find the average of an array
     *
     * @param data the array
     *
     * @return the average of all the values in the array
     */
    fun arrayAvg(data: FloatArray): Float {
        var sum = 0f
        for (value in data) sum += value
        return sum / data.size
    }

    /**
     * Find the N highest peaks in an array
     *
     * @param data the array
     * @param numPeaks the number of peaks to find
     *
     * @return the indices of the N highest peaks in the array, in descending order
     */
    fun findHighestPeaks(data: FloatArray, numPeaks: Int): IntArray? {
        val peaks = IntArray(numPeaks)
        Arrays.fill(peaks, -1)
        val tempArray = FloatArray(data.size)
        System.arraycopy(data, 0, tempArray, 0, data.size)
        val sorted: IntArray = Sort.getSortedIndices(tempArray)
        var peaksFound = 0
        for (i in sorted.indices.reversed()) {
            val maxIndex = sorted[i]
            if (maxIndex != 0 && maxIndex != sorted.size - 1) {
                if (data[maxIndex - 1] + data[maxIndex + 1] < 2 * data[maxIndex] && data[maxIndex] > data[maxIndex - 1] && data[maxIndex] > data[maxIndex + 1]
                ) {
                    peaks[peaksFound++] = maxIndex
                    if (peaksFound >= numPeaks) break
                }
            }
        }
        return peaks
    }

    /**
     * Find the N lowest peaks in an array
     *
     * @param data the array
     * @param numPeaks the number of peaks to find
     *
     * @return the indices of the N lowest peaks in the array, in ascending order
     */
    fun findLowestPeaks(data: FloatArray, numPeaks: Int): IntArray? {
        val peaks = IntArray(numPeaks)
        val tempArray = FloatArray(data.size)
        System.arraycopy(data, 0, tempArray, 0, data.size)
        val sorted: IntArray = Sort.getSortedIndices(tempArray)
        var peaksFound = 0
        for (index in sorted) {
            if (index != 0 && index != sorted.size - 1) {
                if (data[index - 1] + data[index + 1] < 2 * data[index] && data[index] > data[index - 1] && data[index] > data[index + 1]
                ) {
                    peaks[peaksFound++] = index
                    if (peaksFound >= numPeaks) break
                }
            }
        }
        return peaks
    }

    /**
     * Find the first N peaks in an array
     *
     * @param data the array
     * @param numPeaks the number of peaks to find
     *
     * @return the indices of the first N peaks in the array, in the order they appear
     */
    fun findOrderedPeaks(data: FloatArray, numPeaks: Int): IntArray? {
        val peaks = IntArray(numPeaks)
        Arrays.fill(peaks, 0)
        var peaksFound = 0
        for (i in 1 until data.size - 1) {
            if (data[i] > data[i - 1] && data[i] > data[i + 1]) {
                peaks[peaksFound++] = i
                if (peaksFound >= numPeaks) break
            }
        }
        return peaks
    }


    object Sort {
        fun getSortedIndices(data: FloatArray): IntArray {
            val index = IntArray(data.size)
            for (i in data.indices) index[i] = i
            sort(index, data)
            return index
        }

        fun sort(toSort: IntArray, sortBy: FloatArray) {
            val p = 0
            val r = toSort.size - 1
            mergeSort(toSort, sortBy, p, r)
        }

        fun mergeSort(
            toSort: IntArray,
            sortBy: FloatArray,
            p: Int,
            r: Int
        ) {
            if (p < r) {
                val q = (p + r) / 2
                val temp = Arrays.copyOfRange(toSort, p, r + 1)
                mergeSort(toSort, temp, sortBy, p, q)
                mergeSort(toSort, temp, sortBy, q + 1, r)
                merge(temp, toSort, sortBy, p, q, r)
            }
        }

        private fun mergeSort(
            toSort: IntArray,
            temp: IntArray,
            sortBy: FloatArray,
            p: Int,
            r: Int
        ) {
            if (p < r) {
                val q = (p + r) / 2
                mergeSort(temp, toSort, sortBy, p, q)
                mergeSort(temp, toSort, sortBy, q + 1, r)
                merge(toSort, temp, sortBy, p, q, r)
            }
        }

        private fun merge(
            toSort: IntArray,
            temp: IntArray,
            sortBy: FloatArray,
            p: Int,
            q: Int,
            r: Int
        ) {
            var k = p
            var L = p
            var R = q + 1
            while (L <= q && R <= r) {
                temp[k++] =
                    if (sortBy[toSort[R]] < sortBy[toSort[L]]) toSort[R++] else toSort[L++]
            }
            System.arraycopy(toSort, L, temp, k, q - L + 1)
            System.arraycopy(toSort, R, temp, k, r - R + 1)
        }
    }
}
