package ru.swetophor;

import static ru.swetophor.celestialmechanics.Mechanics.CIRCLE;

public class Settings {
    static int edgeHarmonic = 108;
    static int orbsDivider = 30;
    static boolean halfOrbsForDoubles = true;

    public static int getEdgeHarmonic() { return edgeHarmonic; }

    public static void setEdgeHarmonic(int edgeHarmonic) { Settings.edgeHarmonic = edgeHarmonic; }

    public static int getOrbsDivider() { return orbsDivider; }

    public static void setOrbsDivider(int orbsDivider) { Settings.orbsDivider = orbsDivider; }

    public static double getOrbs() { return CIRCLE / orbsDivider; }

    public static void disableHalfOrbForDoubles() { halfOrbsForDoubles = false; }

    public static void enableHalfOrbForDoubles() { halfOrbsForDoubles = true; }

}
