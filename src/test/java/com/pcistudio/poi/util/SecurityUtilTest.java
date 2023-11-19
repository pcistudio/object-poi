package com.pcistudio.poi.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @Test
    void testResume() {
        assertEquals("hola ...", SecurityUtil.resume("hola mundo", 8));

        assertEquals("hola", SecurityUtil.resume("hola", 8));
        assertEquals("hola", SecurityUtil.resume("hola", 4));
    }
}