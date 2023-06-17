package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Mechanics;

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
    private static int edgeHarmonic = 108;
    private static int orbsDivisor = 30;
    private static boolean halfOrbsForDoubles = true;
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

    public static void editSettings() {
        showSettingsMenu();

        while (true) {
            String command = Application.KEYBOARD.nextLine();
            if (command.isBlank())
                break;
            int delimiter = command.indexOf("=");
            if (delimiter == -1) {
                System.out.println("Команда должна содержать оператор '='");
                continue;
            }
            String parameter = command.substring(0, delimiter).trim();
            String value = command.substring(delimiter + 1).trim();
            try {
                switch (parameter) {
                    case "1" -> setEdgeHarmonic(Integer.parseInt(value));
                    case "2" -> setOrbDivider(Integer.parseInt(value));
                    case "3" -> {
                        if (positiveAnswer(value)) enableHalfOrbForDoubles();
                        if (negativeAnswer(value)) disableHalfOrbForDoubles();
                    }
                    case "4" -> {
                        if (positiveAnswer(value)) autosave = true;
                        if (negativeAnswer(value)) autosave = false;
                    }
                    default -> System.out.println("Введи номер существующего параметра, а не " + parameter);
                }
            } catch (NumberFormatException e) {
                System.out.println("Не удалось прочитать значение.");
            }
        }
    }

    private void performChange(String input, Consumer yesAction, Consumer noAction) {

    }

    private static boolean negativeAnswer(String value) {
        return noValues.contains(value.toLowerCase());
    }

    private static boolean positiveAnswer(String value) {
        return yesValues.contains(value.toLowerCase());
    }

    private static void showSettingsMenu() {
        String MENU = """
                                * НАСТРОЙКИ *
                           
                1: крайняя гармоника: %d
                2: делитель для первичного орбиса: %d
                             (первичный орбис = %s)
                3: для двойных карт орбис уменьшен вдвое: %s
                4: автосохранение стола при выходе: %s
                5: файл загрузки при старте: %s
                            
                    _   _   _   _   _   _   _   _   _
                < введи новое как "номер_параметра = значение"
                        или пустой ввод для выхода >
                            
                """;
        System.out.println(Decorator.singularFrame(
                MENU.formatted(edgeHarmonic,
                        orbsDivisor,
                        Mechanics.secondFormat(getPrimalOrb(), false),
                        halfOrbsForDoubles ? "да" : "нет",
                        autosave ? "да" : "нет",
                        autoloadFile)
        ));
    }

}
