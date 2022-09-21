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
        position = normalizeCoordinate(position);
        if (position < 30) return ARIES.symbol;
        else if (position < 60) return TAURUS.symbol;
        else if (position < 90) return GEMINI.symbol;
        else if (position < 120) return CANCER.symbol;
        else if (position < 150) return LEO.symbol;
        else if (position < 180) return VIRGO.symbol;
        else if (position < 210) return LIBRA.symbol;
        else if (position < 240) return SCORPIO.symbol;
        else if (position < 270) return SAGITTARIUS.symbol;
        else if (position < 300) return CAPRICORN.symbol;
        else if (position < 330) return AQUARIUS.symbol;
        else return PISCES.symbol;
    }
}
