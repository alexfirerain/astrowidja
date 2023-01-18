package ru.swetophor.celestialmechanics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MechanicsTest {

    @Test
    void zodiacDegree() {
        assertEquals("16°♑", CelestialMechanics.zodiacDegree(285.4));

    }
}