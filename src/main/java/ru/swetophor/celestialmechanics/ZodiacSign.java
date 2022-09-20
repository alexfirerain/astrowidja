package ru.swetophor.celestialmechanics;

import static ru.swetophor.celestialmechanics.Mechanics.normalizeCoordinate;

public enum ZodiacSign {
    ОВЕН("♈"),
    ТЕЛЕЦ("♉"),
    БЛИЗНЕЦЫ("♊"),
    РАК("♋"),
    ЛЕВ("♌"),
    ДЕВА("♍"),
    ВЕСЫ("♎"),
    СКОРПИОН("♏"),
    СТРЕЛЕЦ("♐"),
    КОЗЕРОГ("♑"),
    ВОДОЛЕЙ("♒"),
    РЫБЫ("♓");

    private final String symbol;

    ZodiacSign(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static String pointsZodium(double position) {
        position = normalizeCoordinate(position);
        if (position < 30) return ОВЕН.symbol;
        else if (position < 60) return ТЕЛЕЦ.symbol;
        else if (position < 90) return БЛИЗНЕЦЫ.symbol;
        else if (position < 120) return РАК.symbol;
        else if (position < 150) return ЛЕВ.symbol;
        else if (position < 180) return ДЕВА.symbol;
        else if (position < 210) return ВЕСЫ.symbol;
        else if (position < 240) return СКОРПИОН.symbol;
        else if (position < 270) return СТРЕЛЕЦ.symbol;
        else if (position < 300) return КОЗЕРОГ.symbol;
        else if (position < 330) return ВОДОЛЕЙ.symbol;
        else return РЫБЫ.symbol;
    }
}
