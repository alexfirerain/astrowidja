package ru.swetophor.celestialmechanics;

import static ru.swetophor.celestialmechanics.Mechanics.normalizeCoordinate;

/**
 * Представление зодиакального знака, содержит символ.
 */
public enum ZodiacSign {
    ARIES("♈"),
    TAURUS("♉"),
    GEMINI("♊"),
    CANCER("♋"),
    LEO("♌"),
    VIRGO("♍"),
    LIBRA("♎"),
    SCORPIO("♏"),
    SAGITTARIUS("♐"),
    CAPRICORN("♑"),
    AQUARIUS("♒"),
    PISCES("♓");

    private final String symbol;

    ZodiacSign(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static String pointsZodium(double position) {
        return switch ((int) normalizeCoordinate(position) / 30) {
            case 0 -> ARIES.symbol;
            case 1 -> TAURUS.symbol;
            case 2 -> GEMINI.symbol;
            case 3 -> CANCER.symbol;
            case 4 -> LEO.symbol;
            case 5 -> VIRGO.symbol;
            case 6 -> LIBRA.symbol;
            case 7 -> SCORPIO.symbol;
            case 8 -> SAGITTARIUS.symbol;
            case 9 -> CAPRICORN.symbol;
            case 10 -> AQUARIUS.symbol;
            case 11 -> PISCES.symbol;
            default -> throw new IllegalStateException("Unexpected value: " + position);
        };
    }

}
