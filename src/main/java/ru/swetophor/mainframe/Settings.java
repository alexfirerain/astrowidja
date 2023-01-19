package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Mechanics;

import java.util.Set;

import static ru.swetophor.celestialmechanics.CelestialMechanics.CIRCLE;

/**
 * Класс-хранилище статических глобальных переменных АстроВидьи.
 */
public class Settings {
    private static final Set<String> yesValues = Set.of("да", "+", "yes", "true", "д", "y", "t");
    private static final Set<String> noValues = Set.of("нет", "-", "no", "false", "н", "n", "f");
    private static int edgeHarmonic = 108;
    private static int orbsDivisor = 30;
    private static boolean halfOrbsForDoubles = true;

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
            String command = Application.keyboard.nextLine();
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
                        if (yesValues.contains(value.toLowerCase())) enableHalfOrbForDoubles();
                        if (noValues.contains(value.toLowerCase())) disableHalfOrbForDoubles();
                    }
                    default -> System.out.println("Введи номер существующего параметра.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Не удалось прочитать значение.");
            }
        }
    }

    private static void showSettingsMenu() {
        String MENU = """
                                * НАСТРОЙКИ *
                           
                1: крайняя гармоника: %d
                2: делитель для первичного орбиса: %d
                             (первичный орбис = %s)
                3: для двойных карт орбис уменьшен вдвое: %s
                            
                    _   _   _   _   _   _   _   _   _
                < введи новое как "номер_параметра = значение"
                        или пустой ввод для выхода >
                            
                """;
        System.out.println(Decorator.frameText(
                MENU.formatted(edgeHarmonic, orbsDivisor,
                        Mechanics.secondFormat(getPrimalOrb(), false),
                        halfOrbsForDoubles ? "да" : "нет"), 30, 80,
                '┌', '─', '┐',
                '│', '└', '┘'));
    }

}
