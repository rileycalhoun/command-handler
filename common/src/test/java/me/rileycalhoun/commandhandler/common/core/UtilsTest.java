package me.rileycalhoun.commandhandler.common.core;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @SuppressWarnings("unused")
    private static class Methods {
        public void voidMethod() {}
        public String stringMethod() { return "ok"; }
    }

    @Test
    void colorizeTranslatesAmpersandCodes() {
        assertEquals("\u00A7aHello", Utils.colorize("&aHello"));
    }

    @Test
    void colorizeLowercasesTheCodeCharacter() {
        assertEquals("\u00A7a", Utils.colorize("&A"));
    }

    @Test
    void colorizeLeavesInvalidCodesUntouched() {
        assertEquals("&z", Utils.colorize("&z"));
    }

    @Test
    void colorizeLeavesTrailingAmpersandUntouched() {
        assertEquals("fish &", Utils.colorize("fish &"));
    }

    @Test
    void firstNotNullReturnsFirstNonNullValue() {
        assertEquals("b", Utils.firstNotNull(null, "b", "c"));
    }

    @Test
    void firstNotNullReturnsNullWhenAllNull() {
        assertNull(Utils.firstNotNull(null, null));
    }

    @Test
    void addElementsInFrontPrependsAndShifts() {
        String[] result = Utils.addElementsInFront(new String[]{"b", "c"}, "a");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    @Test
    void nThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> Utils.n(null));
    }

    @Test
    void cThrowsIllegalArgumentWithMessageOnNull() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Utils.c(null, "must not be null"));
        assertEquals("must not be null", e.getMessage());
    }

    @Test
    void immutableListCannotBeModified() {
        List<String> list = Utils.immutable("a", "b");
        assertThrows(UnsupportedOperationException.class, () -> list.add("c"));
    }

    @Test
    void checkReturnsRejectsVoidMethods() throws Exception {
        Method m = Methods.class.getMethod("voidMethod");
        assertThrows(IllegalArgumentException.class, () -> Utils.checkReturns(m));
    }

    @Test
    void checkReturnsAcceptsNonVoidMethods() throws Exception {
        Method m = Methods.class.getMethod("stringMethod");
        assertDoesNotThrow(() -> Utils.checkReturns(m));
    }

    @Test
    void getTypeUnwrapsClassInstances() {
        assertEquals(String.class, Utils.getType("text"));
        assertEquals(Integer.class, Utils.getType(Integer.class));
    }
}
