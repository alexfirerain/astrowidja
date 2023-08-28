package ru.swetophor.mainframe;

import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

import static ru.swetophor.celestialmechanics.CelestialMechanics.CIRCLE;

/**
 * Класс-хранилище статических глобальных переменных АстроВидьи.
 */
public class Settings {
    static final Set<String> yesValues = Set.of("да", "+", "yes", "true", "д", "y", "t", "1");
    static final Set<String> noValues = Set.of("нет", "-", "no", "false", "н", "n", "f", "0");
    static int edgeHarmonic = 108;
    static int orbsDivisor = 30;
    static boolean halfOrbsForDoubles = true;
    public static boolean autosave = false;

    public static String autoloadFile = "сохранение вс 15 января .23 15-03.awb";
    public static boolean autoloadEnabled = true;

    private static File settingsSourceFile = new File("settings.ini");

    public static boolean isHalfOrbsForDoubles() {
        return halfOrbsForDoubles;
    }

    public static int getEdgeHarmonic() {
        return edgeHarmonic;
    }

    public static void setEdgeHarmonic(int edgeHarmonic) {
        Settings.edgeHarmonic = edgeHarmonic;
    }

    public static int getOrbDivisor() {
        return orbsDivisor;
    }

    public static void setOrbDivider(int orbsDivider) {
        Settings.orbsDivisor = orbsDivider;
    }

    public static double getPrimalOrb() {
        return CIRCLE / orbsDivisor;
    }

    public static void disableHalfOrbForDoubles() {
        halfOrbsForDoubles = false;
    }

    public static void enableHalfOrbForDoubles() {
        halfOrbsForDoubles = true;
    }

    private void performChange(String input, Consumer yesAction, Consumer noAction) {

    }

}
