package com.thom.elevatorcontroller.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CallTest {

    @Test
    void hallUpFactoryBuildsValidCall() {
        Call call = Call.hallUp(3);
        assertEquals(3, call.floor());
        assertEquals(true, call.hallUp());
        assertEquals(false, call.hallDown());
        assertEquals(false, call.car());
    }

    @Test
    void hallDownFactoryBuildsValidCall() {
        Call call = Call.hallDown(7);
        assertEquals(7, call.floor());
        assertEquals(false, call.hallUp());
        assertEquals(true, call.hallDown());
        assertEquals(false, call.car());
    }

    @Test
    void rejectsOutOfRangeFloor() {
        assertThrows(IllegalArgumentException.class, () -> Call.hallUp(-1));
        assertThrows(IllegalArgumentException.class, () -> Call.hallDown(20));
    }

    @Test
    void rejectsMultipleTypesSet() {
        assertThrows(IllegalArgumentException.class, () -> new Call(true, true, false, 0));
        assertThrows(IllegalArgumentException.class, () -> new Call(true, false, true, 0));
        assertThrows(IllegalArgumentException.class, () -> new Call(false, true, true, 0));
    }

    @Test
    void rejectsNoTypesSet() {
        assertThrows(IllegalArgumentException.class, () -> new Call(false, false, false, 0));
    }
}
