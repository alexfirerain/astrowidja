package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;
import ru.swetophor.celestialmechanics.Mechanics;
import ru.swetophor.celestialmechanics.Synastry;

import java.util.Scanner;

import static ru.swetophor.mainframe.Application.DESK;
import static ru.swetophor.mainframe.Application.astroSource;
import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Settings.*;

public class CommandLineMainUI implements MainUI {

    static final Scanner KEYBOARD = new Scanner(System.in);

    /**
     * Цикл работы с картой.
     * Предоставляет действия, которые можно выполнить с картой: просмотр статистики,
     * сохранение в список (файл), построение средней и синастрической карт.
     * Пустой ввод означает выход из цикла и метода.
     *
     * @param chartObject карта, являющаяся предметом работы.
     */
    public void workCycle(ChartObject chartObject) {
        String CHART_MENU = """
                    действия с картой:
                "-> имя_файла"   = сохранить в файл
                "+карта" = построить синастрию
                "*карта" = построить композит
                 
                "1" = о положениях астр
                "2" = о резонансах
                "3" = о паттернах кратко
                "4" = о паттернах со статистикой
                """;
        print(chartObject.getCaption());
        print(chartObject.getAstrasList());
        printInFrame(CHART_MENU);
        String input;
        while (true) {
            input = getUserInput();
            if (input == null || input.isBlank())
                return;

            if (input.startsWith("->")) {
                Storage.putChartToBase(chartObject, input.substring(2).trim());

            } else if (input.startsWith("+") && chartObject instanceof Chart) {
                ChartObject counterpart = null;
                String order = input.substring(1).trim();
                try {
                    counterpart = DESK.findChart(order, "на столе");
                } catch (ChartNotFoundException e) {
                    print("Карта '%s' не найдена: %s".formatted(order, e.getLocalizedMessage()));
                }
                if (counterpart instanceof Chart)
                    Application.addChart(new Synastry((Chart) chartObject, (Chart) counterpart));

            } else if (input.startsWith("*") && chartObject instanceof Chart) {
                ChartObject counterpart = null;
                String order = input.substring(1).trim();
                try {
                    counterpart = DESK.findChart(order, "на столе");
                } catch (ChartNotFoundException e) {
                    print("Карта '%s' не найдена: %s".formatted(order, e.getLocalizedMessage()));
                }
                if (counterpart instanceof Chart)
                    Application.addChart(Mechanics.composite((Chart) chartObject, (Chart) counterpart));
            }
            else switch (input) {
                    case "1" -> print(chartObject.getAstrasList());
                    case "2" -> print(chartObject.getAspectTable());
                    case "3" -> print(chartObject.resonanceAnalysis(getEdgeHarmonic()));
                    case "4" -> print(chartObject.resonanceAnalysisVerbose(getEdgeHarmonic()));
                    default -> printInFrame(CHART_MENU);
                }
        }
    }

    private void showSettingsMenu() {
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
        System.out.println(singularFrame(
                MENU.formatted(edgeHarmonic,
                        orbsDivisor,
                        Mechanics.secondFormat(getPrimalOrb(), false),
                        halfOrbsForDoubles ? "да" : "нет",
                        autosave ? "да" : "нет",
                        autoloadFile)
        ));
    }

    private static boolean negativeAnswer(String value) {
        return noValues.contains(value.toLowerCase());
    }

    private static boolean positiveAnswer(String value) {
        return yesValues.contains(value.toLowerCase());
    }

    public void editSettings() {
        showSettingsMenu();

        while (true) {
            String command = getUserInput();
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

    /**
     * Выводит на экран список карт, лежащих на {@link Application#DESK столе}, то есть загруженных в программу.
     */
    @Override
    public void listCharts() {
        printInFrame(DESK.isEmpty() ?
                "Ни одной карты не загружено." :
                DESK.toString()
        );
    }


    @Override
    public void mainCycle() {
        listCharts();
        String MENU = """
                1. карты на столе
                2. настройки
                3. списки карт
                4. работа с картой
                5. добавить карту с клавиатуры
                0. выход
                """;
        boolean exit = false;
        while (!exit) {
            printInDoubleFrame(MENU);
            switch (getUserInput()) {
                case "1" -> listCharts();
                case "2" -> editSettings();
                case "3" -> astroSource.listsCycle();
                case "4" -> Application.takeChart();
                case "5" -> Application.addChart(CommandLineAstroSource.enterChartData());
                case "0" -> exit = true;
            }
        }
        print("Спасибо за ведание резонансов!");
    }

    @Override
    public void welcome() {
        System.out.printf("%sСчитаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n%n",
                asteriskFrame("Начато исполнение АстроВидьи!"),
                getPrimalOrb(), getOrbDivisor(), getEdgeHarmonic());
    }

    /**
     * Получает ввод пользователя.
     * @return строку, введённую юзером с клавиатуры.
     */
    @Override
    public String getUserInput() {
        return KEYBOARD.nextLine();
    }
}
