package io.data2viz.color

import io.data2viz.color.EncodedColors.Companion.category10
import io.data2viz.color.EncodedColors.Companion.category20
import io.data2viz.color.EncodedColors.Companion.category20c
import io.data2viz.color.EncodedColors.Companion.inferno
import io.data2viz.color.EncodedColors.Companion.magma
import io.data2viz.color.EncodedColors.Companion.plasma
import io.data2viz.color.EncodedColors.Companion.viridis
import io.data2viz.core.namespace
import io.data2viz.test.TestBase
import kotlin.browser.document
import kotlin.test.Test

class EncodedColorsTests: TestBase() {

    @Test fun category10() {
        category10.colors.size shouldBe 10
        displaySmallGradient("category10", category10, imageReference = "http://data2viz.io/img/category10.png")
    }

    @Test fun category20() {
        category20.colors.size shouldBe 20
        displaySmallGradient("category20", category20, imageReference = "http://data2viz.io/img/category20.png")
    }

    @Test fun category20b() {
        category20.colors.size shouldBe 20
        displaySmallGradient("category20", category20, imageReference = "http://data2viz.io/img/category20b.png")
    }

    @Test fun category20c() {
        category20c.colors.size shouldBe 20
        displaySmallGradient("category20c", category20c, imageReference = "http://data2viz.io/img/category20c.png")
    }

    @Test fun viridis ()    { testAndGraph("viridis", viridis, imageReference = "http://data2viz.io/img/viridis.png") }
    @Test fun magma ()      { testAndGraph("magma",   magma,   imageReference = "http://data2viz.io/img/magma.png") }
    @Test fun inferno ()    { testAndGraph("inferno", inferno, imageReference = "http://data2viz.io/img/inferno.png") }
    @Test fun plasma ()     { testAndGraph("plasma",  plasma,  imageReference = "http://data2viz.io/img/plasma.png") }


    fun displaySmallGradient(context: String, colors: EncodedColors, imageReference: String? = null) {
        val width = 20 * colors.colors.size
        document.body?.appendChild(document.createElement("h2").appendChild(document.createTextNode(context)))
        document.body?.appendChild(
                node("svg").apply {
                    setAttribute("width", "$width")
                    setAttribute("height", "20")
                    colors.colors.forEachIndexed { index, color ->
                        appendChild(
                                node("rect").apply {
                                    setAttribute("fill", color.rgbHex)
                                    setAttribute("x", "${index * 20}")
                                    setAttribute("y", "0")
                                    setAttribute("width", "20")
                                    setAttribute("height", "20")
                                }
                        )
                    }
                }
        )
        if (imageReference != null) {
            val div = document.createElement("div")
            div.appendChild(
                    document.createElement("img").apply {
                        setAttribute("src", imageReference)
                        setAttribute("height", "20")
                        setAttribute("width", "$width")
                    }
            )
            document.body?.appendChild(div)
        }
    }

    fun testAndGraph(context: String, gradient: EncodedColors, imageReference: String? = null) {
        document.body?.appendChild(document.createElement("h2").appendChild(document.createTextNode(context)))


        //generate a list of 6, 10, 20 colors from the gradient.
        /*listOf(6, 10, 20).forEach { size ->
            val colors = (0..size - 1)
                    .map { gradient.color(it.toDouble() / (size - 1)) }
            println(colors.joinToString(transform = { it.rgbHex }))
        }*/

        document.body?.appendChild(
                node("svg").apply {
                    setAttribute("width", "400")
                    setAttribute("height", "20")
                    (0..399).forEach {
                        appendChild(
                                node("rect").apply {
                                    setAttribute("fill", gradient.color(it.toDouble() / 400).rgbHex)
                                    setAttribute("x", "${it}")
                                    setAttribute("y", "0")
                                    setAttribute("width", "1")
                                    setAttribute("height", "20")
                                }
                        )
                    }
                }
        )

        if (imageReference != null) {
            val div = document.createElement("div")
            div.appendChild(
                    document.createElement("img").apply {
                        setAttribute("src", imageReference)
                        setAttribute("height", "20")
                        setAttribute("width", "400")
                    }
            )
            document.body?.appendChild(div)
        }
    }

    fun node(name: String) = document.createElementNS(namespace.svg, name)

}
