package io.data2viz.voronoi

import kotlin.math.atan2


internal var wCells: Array<Cell?>? = null

class Cell(val site: Site) {

    /**
     * List of index of edges of the current Cell.
     */
    val halfedges = mutableListOf<Int>()


    companion object {
        fun createCell(site: Site) {
            wCells!![site.index] = Cell(site)
        }
    }

    fun halfedgeAngle(edge: Edge): Double {
        var va = edge.left.pos
        var vb = edge.right?.pos

        if (site.pos === vb) {
            vb = va
            va = site.pos
        }

        if (vb != null) {
            return atan2(vb.y - va.y, vb.x - va.x)
        }
        if (site.pos === va) {
            va = edge.end!!
            vb = edge.start
        } else {
            va = edge.start!!
            vb = edge.end
        }
        return atan2(va.x - vb!!.x, vb.y - va.y)
    }

    fun halfedgeStart(edge: Edge) = if (edge.left !== site) edge.end else edge.start
    fun halfedgeEnd(edge: Edge) = if (edge.left === site) edge.end else edge.start

    fun cellHalfedgeStart(edge:Edge) = if (edge.left !== site) edge.end else edge.start
    fun cellHalfedgeEnd  (edge:Edge) = if (edge.left === site) edge.end else edge.start

}

/**
 * Sort all cells halfedges, necessary for polygons.
 */
fun sortCellHalfedges() {

    var edgeCount:Int
    var halfedges: MutableList<Int>
    var cell: Cell

    for (cellIndex in 0..wCells!!.lastIndex) {
        cell = wCells!![cellIndex]!!
        halfedges = cell.halfedges
        edgeCount = halfedges.size

        val indexes: Array<Int> = Array(edgeCount) { it }
        val angles: Array<Double> = Array(edgeCount) { cell.halfedgeAngle(wEdges[halfedges[it]]!!)}
        indexes.sortWith(object : Comparator<Int> { override fun compare(a: Int, b: Int) = angles[b].compareTo(angles[a]) })
        val temp: Array<Int> = Array(edgeCount) {halfedges[indexes[it]]}
        (0..edgeCount-1).forEach { halfedges[it] = temp[it] }
    }

}
