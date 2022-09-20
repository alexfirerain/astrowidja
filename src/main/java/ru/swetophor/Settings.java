package ru.swetophor;

import static ru.swetophor.celestialmechanics.Mechanics.CIRCLE;

public class Settings {
    static int edgeHarmonic = 108;
    static int orbisDivider = 30;
    static boolean halfOrbisForDoubles = true;

    public static int getEdgeHarmonic() { return edgeHarmonic; }

    public static void setEdgeHarmonic(int крайняяГармоника) { Settings.edgeHarmonic = крайняяГармоника; }

    public static int getOrbisDivider() { return orbisDivider; }

    public static void setOrbisDivider(int делительОрбиса) { Settings.orbisDivider = делительОрбиса; }

    public static double getOrbis() { return CIRCLE / orbisDivider; }

}
