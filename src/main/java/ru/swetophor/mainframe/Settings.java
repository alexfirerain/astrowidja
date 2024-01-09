package ru.swetophor.mainframe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static ru.swetophor.celestialmechanics.CelestialMechanics.CIRCLE;
import static ru.swetophor.mainframe.Decorator.print;

/**
 * Класс-хранилище статических глобальных переменных АстроВидьи.
 */
public class Settings {
    private static final File settingsSourceFile = new File("settings.ini");
    private static final Map<String, String> settings = readSettings();


    public static Map<String, String> readSettings() {
        Map<String, String> settings = new HashMap<>();
        try {
            String source = Files.readString(Objects.requireNonNull(settingsSourceFile).toPath());
            System.out.println(source);
            for (String line : source.split("\n")) {
                if (line.isBlank() || line.startsWith("#")) continue;
                int operatorPosition = line.indexOf("=");
                if (operatorPosition == -1) continue;
                String property = line.substring(0, operatorPosition).trim();
                String value = line.substring(operatorPosition + 1).trim();
                if (property.isBlank() || value.isBlank()) continue;
                settings.put(property, value);
                System.out.println(property + " = " + value);
            }
        } catch (IOException | NullPointerException e) {
            print("Файл настроек не найден.");
        }
        return settings;
    }
    public static void saveSettings() {
        StringBuilder drop = new StringBuilder();
        for (Map.Entry<String, String> property : settings.entrySet())
            drop.append("%s = %s%n".formatted(property.getKey(), property.getValue()));
        try (FileWriter writer = new FileWriter(settingsSourceFile, false)) {
            writer.write(drop.toString());
            writer.flush();
        } catch (IOException e) {
            print("Не удалось сохранить настройки.");
        }
    }
    private static final int EDGE_HARMONIC_DEFAULT = 108;
    private static final int ORBS_DIVISOR_DEFAULT = 30;
    private static final boolean HALF_ORBS_FOR_DOUBLES_DEFAULT = true;
    private static final boolean AUTOSAVE_DEFAULT = false;
    private static final String AUTOLOAD_FILE_DEFAULT = "сохранение вс 15 января .23 15-03.awb";
    private static final boolean AUTOLOAD_ENABLED_DEFAULTS = true;


    public static boolean isHalfOrbsForDoubles() {
        return getBoolProperty("ORBES_DIMIDII_DUPLICIBUS").orElse(HALF_ORBS_FOR_DOUBLES_DEFAULT);
    }

    public static int getEdgeHarmonic() {
        return getIntProperty("HARMONICA_ULTIMA").orElse(EDGE_HARMONIC_DEFAULT);
    }

    public static boolean isAutosave() {
        return getBoolProperty("AUTOSAVE").orElse(AUTOSAVE_DEFAULT);
    }

    public static void setAutosave(boolean turnAutosaveOn) {
        settings.put("AUTOSAVE", String.valueOf(turnAutosaveOn));
    }

    public static String getAutoloadFile() {
        return getStringProperty("AUTOLOAD_FILE").orElse(AUTOLOAD_FILE_DEFAULT);
    }

    public static void setAutoloadFile(String autoloadFile) {
        settings.put("AUTOLOAD_FILE", autoloadFile);
    }

    public static boolean isAutoloadEnabled() {
        return getBoolProperty("AUTOLOAD_ENABLED").orElse(AUTOLOAD_ENABLED_DEFAULTS);
    }

    private static Optional<Integer> getIntProperty(String property) {
        String value = settings.get(property);
        if (value == null) return Optional.empty();
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static Optional<Boolean> getBoolProperty(String property) {
        String value = settings.get(property);
        if (value == null) return Optional.empty();
        return Optional.of(Boolean.parseBoolean(value));
    }

    private static Optional<String> getStringProperty(String property) {
        return Optional.ofNullable(settings.get(property));
    }

    public static void setEdgeHarmonic(int edgeHarmonic) {
        settings.put("HARMONICA_ULTIMA", String.valueOf(edgeHarmonic));
    }

    public static int getOrbDivisor() {
        return getIntProperty("ORBS_DIVISOR").orElse(ORBS_DIVISOR_DEFAULT);
    }

    public static void setOrbDivider(int orbsDivisor) {
        settings.put("ORBS_DIVISOR", String.valueOf(orbsDivisor));
    }

    public static double getPrimalOrb() {
        return CIRCLE / getOrbDivisor();
    }

    public static void disableHalfOrbForDoubles() {
        settings.put("ORBES_DIMIDII_DUPLICIBUS", "false");
    }

    public static void enableHalfOrbForDoubles() {
        settings.put("ORBES_DIMIDII_DUPLICIBUS", "true");
    }

    private void performChange(String input, Consumer yesAction, Consumer noAction) {

    }

}
