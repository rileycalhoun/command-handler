package me.rileycalhoun.commandhandler.common.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrimitivesTest {

    @Test
    void wrapConvertsPrimitivesToWrappers() {
        assertEquals(Integer.class, Primitives.wrap(int.class));
        assertEquals(Boolean.class, Primitives.wrap(boolean.class));
        assertEquals(Void.class, Primitives.wrap(void.class));
    }

    @Test
    void wrapLeavesNonPrimitivesAlone() {
        assertEquals(String.class, Primitives.wrap(String.class));
        assertEquals(Integer.class, Primitives.wrap(Integer.class));
    }

    @Test
    void unwrapConvertsWrappersToPrimitives() {
        assertEquals(int.class, Primitives.unwrap(Integer.class));
        assertEquals(long.class, Primitives.unwrap(Long.class));
    }

    @Test
    void unwrapLeavesNonWrappersAlone() {
        assertEquals(String.class, Primitives.unwrap(String.class));
        assertEquals(int.class, Primitives.unwrap(int.class));
    }

    @Test
    void isWrapperTypeRecognizesOnlyWrappers() {
        assertTrue(Primitives.isWrapperType(Integer.class));
        assertFalse(Primitives.isWrapperType(int.class));
        assertFalse(Primitives.isWrapperType(String.class));
    }

    @Test
    void allPrimitiveAndWrapperSetsHaveNineEntries() {
        assertEquals(9, Primitives.allPrimitiveTypes().size());
        assertEquals(9, Primitives.allWrapperTypes().size());
    }

    @Test
    void wrapAndUnwrapRejectNull() {
        assertThrows(NullPointerException.class, () -> Primitives.wrap(null));
        assertThrows(NullPointerException.class, () -> Primitives.unwrap(null));
    }
}
