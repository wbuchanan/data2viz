package io.data2viz.path

import javafx.scene.shape.*
import kotlin.math.*


class PathJfx: PathAdapter {
    
    val path = Path()
    
    private var x0:Double = 0.0
    private var y0:Double = 0.0
    private var x1:Double? = null
    private var y1:Double? = null


    override fun moveTo(x: Double, y: Double) {
        x0 = x
        y0 = y
        x1 = x
        y1 = y
        path.elements += MoveTo(x,y)
    }

    override fun lineTo(x: Double, y: Double) {
        x1 = x
        y1 = y
        path.elements += LineTo(x, y)
    }

    override fun closePath() {
        if(x1 != null){
            x1 = x0
            y1 = y0
            path.elements += ClosePath()
        }
    }

    override fun quadraticCurveTo(x1: Double, y1: Double, x: Double, y: Double) {
        this.x1 = x
        this.y1 = y
        path.elements += QuadCurveTo(x1,y1,x,y)
    }

    override fun bezierCurveTo(x1: Double, y1: Double, x2: Double, y2: Double, x: Double, y: Double) {
        this.x1 = x
        this.y1 = y
        path.elements += CubicCurveTo(x1,y1, x2, y2, x, y)

    }

    override fun arcTo(fromX: Double, fromY: Double, toX: Double, toY: Double, radius: Double) {
        val r = radius
        if (r < 0.0) throw IllegalArgumentException("Negative radius:" + radius)

        val x1 = fromX
        val y1 = fromY
        val x2 = toX
        val y2 = toY

        val x0 = this.x1 ?: .0
        val y0 = this.y1 ?: .0

        val x21 = x2 - x1
        val y21 = y2 - y1
        val x01 = x0 - x1
        val y01 = y0 - y1
        val l01_2 = x01 * x01 + y01 * y01

        with(this.x1){
            //path is empty, introduce private function?
            if(this == null){
                // Is this path empty? Move to (x1,y1).
                this@PathJfx.x1 = x1
                this@PathJfx.y1 = y1
                path.elements += MoveTo(x1,y1)
            }
            // Or, is (x1,y1) coincident with (x0,y0)? Do nothing.
            else if (l01_2 <= epsilon){}

            // Or, are (x0,y0), (x1,y1) and (x2,y2) collinear?
            // Equivalently, is (x1,y1) coincident with (x2,y2)?
            // Or, is the radius zero? Line to (x1,y1).
            else if (abs(y01 * x21 - y21 * x01) <= epsilon || r == .0) {
                this@PathJfx.x1 = x1
                this@PathJfx.y1 = y1
                path.elements += LineTo(x1, y1)
            }

            // Otherwise, draw an arc!
            else {
                val x20 = x2 - x0
                val y20 = y2 - y0
                val l21_2 = x21 * x21 + y21 * y21
                val l20_2 = x20 * x20 + y20 * y20
                val l21 = sqrt(l21_2)
                val l01 = sqrt(l01_2)
                val l = r * tan((pi - acos((l21_2 + l01_2 - l20_2) / (2 * l21 * l01))) / 2)
                val t01 = l / l01
                val t21 = l / l21

                // If the start tangent is not coincident with (x0,y0), line to.
                if (abs(t01 - 1) > epsilon) {
                    path.elements += LineTo(x1 + t01 * x01,y1 + t01 * y01)
                }

                this@PathJfx.x1 = x1 + t21 * x21
                this@PathJfx.y1 = y1 + t21 * y21
                val sweepFlag = y01 * x20 > x01 * y20
                path.elements += ArcTo(r,r,0.0, x1, y1, false, sweepFlag)
            }
        }
    }

    override fun arc(
        centerX: Double,
        centerY: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        counterClockWise: Boolean
    ) {
        val r = radius
        if (r < 0.0) throw IllegalArgumentException("Negative radius:" + radius)

        val a0 = startAngle
        val a1 = endAngle
        val x = centerX
        val y = centerY

        val dx = r * cos(a0)
        val dy = r * sin(a0)
        val x0 = x + dx
        val y0 = y + dy
        val cw = !counterClockWise
        var da = if(counterClockWise) a0 - a1 else a1 - a0

        with(x1){

            //path is empty, introduce private function?
            if(this == null)
                path.elements += MoveTo(x0, y0)

            else if (abs(this.toDouble() - x0) > epsilon || abs(y1!!.toDouble() - y0) > epsilon){
                path.elements += LineTo(x0, y0)
            }
        }

        if (r < epsilon) return

        if (da < 0) da = da % tau + tau

        //complete circle
        if (da > tauEpsilon) {
            path.elements += ArcTo(r, r, 0.0, x - dx, y - dy, true, cw)
            x1 = x0
            y1 = y0
            path.elements += ArcTo(r, r, 0.0, x1!!, y1!!, true, cw)
        }

        // Is this arc non-empty? Draw an arc!
        else if (da > epsilon) {
            x1 = x + r * cos(a1)
            y1 = y + r * sin(a1)
            path.elements += ArcTo(r, r, .0, x1!!, y1!!, da >= pi, cw)
        }
    }

    override fun rect(x: Double, y: Double, w: Double, h: Double) {
        x0 = x
        x1 = x
        y0 = y
        y1 = y
        path.elements += MoveTo(x,y)
        path.elements += HLineTo(w)
        path.elements += VLineTo(h)
        path.elements += HLineTo(-w)
        path.elements += ClosePath()
    }

}