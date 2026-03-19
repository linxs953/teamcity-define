package com.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class AppTest {

    @Test
    fun `test greet returns correct greeting`() {
        val app = App()
        val result = app.greet("World")
        assertEquals("Hello, World!", result)
    }

    @Test
    fun `test add returns correct sum`() {
        val app = App()
        assertEquals(5, app.add(2, 3))
        assertEquals(0, app.add(0, 0))
        assertEquals(-1, app.add(1, -2))
    }
}
