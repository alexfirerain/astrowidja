package ru.swetophor.celestialmechanics;

import static ru.swetophor.celestialmechanics.CelestialMechanics.normalizeCoordinate;

/**
 * Представление зодиакального знака, содержит символ.
 */
public enum ZodiacSign {
    ARIES('♈'),
    TAURUS('♉'),
    GEMINI('♊'),
    CANCER('♋'),
    LEO('♌'),
    VIRGO('♍'),
    LIBRA('♎'),
    SCORPIO('♏'),
    SAGITTARIUS('♐'),
    CAPRICORN('♑'),
    AQUARIUS('♒'),
    PISCES('♓');

    private final char symbol;

    ZodiacSign(char symbol) {
        this.symbol = symbol;
    }

    public static char zodiumIcon(double position) {
        return getZodiumOf(position).getSymbol();
    }

    public static ZodiacSign getZodiumOf(double position) {
        return values()[(int) normalizeCoordinate(position) / 30];
//        return switch ((int) normalizeCoordinate(position) / 30) {
//            case 0 -> ARIES;
//            case 1 -> TAURUS;
//            case 2 -> GEMINI;
//            case 3 -> CANCER;
//            case 4 -> LEO;
//            case 5 -> VIRGO;
//            case 6 -> LIBRA;
//            case 7 -> SCORPIO;
//            case 8 -> SAGITTARIUS;
//            case 9 -> CAPRICORN;
//            case 10 -> AQUARIUS;
//            case 11 -> PISCES;
//            default -> throw new IllegalStateException("Unexpected value: " + position);
//        };
    }

    public char getSymbol() {
        return symbol;
    }

}
