package ru.swetophor.celestialmechanics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AstraEntityTest {

    @Test
    void getEntityByName() {
        char expected = '♂';
        char actual = AstraEntity.findSymbolByName("Марс");
        Assertions.assertEquals(expected, actual);
        System.out.println("Symbol for Марс: " + actual);
    }

    @Test
    void findSymbolByName() {
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {
    }
}