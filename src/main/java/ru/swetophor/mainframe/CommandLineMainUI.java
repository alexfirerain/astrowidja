package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;
import ru.swetophor.celestialmechanics.Mechanics;
import ru.swetophor.celestialmechanics.Synastry;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

import static ru.swetophor.mainframe.Application.*;
import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Settings.*;

public class CommandLineMainUI implements MainUI {

    static final Scanner KEYBOARD = new Scanner(System.in);
    static final Set<String> yesValues = Set.of("да", "+", "yes", "true", "д", "y", "t", "1");
    static final Set<String> noValues = Set.of("нет", "-", "no", "false", "н", "n", "f", "0");

    /**
     * Запрашивает, какую карту со {@link Application#DESK стола} взять в работу,
     * т.е. запустить в {@link MainUI#workCycle(ChartObject) цикле процедур для карты}.
     * Если карта не опознана по номеру на столе или имени, сообщает об этом.
     *
     * @return  найденную по номеру или имени карту со стола, или {@code ПУСТО}, если не найдено.
     */
    public ChartObject selectChartOnDesk() {
        print("Укажите карту по имени или номеру на столе: ");
        String order = getUserInput();
        ChartObject taken = null;
        try {
            taken = findOnDesk(order);
        } catch (ChartNotFoundException e) {
            print("Карты '%s' не найдено: %s".formatted(order, e.getLocalizedMessage()));
        }
        return taken;
    }

//    /**
//     * Добавляет карту на {@link Application#DESK стол}.
//     * Если карта с таким именем уже
//     * присутствует, запрашивает решение у юзера.
//     *
//     * @param chart добавляемая карта.
//     * @return  строку, сообщающую состояние операции.
//     */
//    @Override
//    public String addChart(ChartObject chart) {
////        if (DESK.addResolving(chart, "на столе"))
//        return mergeResolving(DESK, chart, "на столе") ?
//                "Карта загружена на стол: " + chart :
//                "Карта не загружена.";
//    }

    /**
     * Добавляет {@link Application#DESK в реестр} произвольное количество карт из аргументов.
     * Если какая-то карта совпадает с уже записанной, у юзера
     * запрашивается решение.
     * @param charts добавляемые карты.
     */
    public void addChart(ChartObject... charts) {
        Arrays.stream(charts).forEach(chart -> print(addChart(chart)));
    }

    /**
     * Цикл работы с картой.
     * Предоставляет действия, которые можно выполнить с картой: просмотр статистики,
     * сохранение в список (файл), построение средней и синастрической карт.
     * Пустой ввод означает выход из цикла и метода.
     *
     * @param chartObject карта, являющаяся предметом работы.
     */
    public void workCycle(ChartObject chartObject) {
        if (chartObject == null) return;

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
                chartRepository.putChartToBase(chartObject, input.substring(2).trim());

            } else if (input.startsWith("+") && chartObject instanceof Chart) {
                ChartObject counterpart;
                String order = input.substring(1).trim();
                try {
                    counterpart = findOnDesk(order);
                    if (counterpart instanceof Chart)
                        print(addChart(new Synastry((Chart) chartObject, (Chart) counterpart)));
                } catch (ChartNotFoundException e) {
                    print("Карта '%s' не найдена: %s".formatted(order, e.getLocalizedMessage()));
                }


            } else if (input.startsWith("*") && chartObject instanceof Chart) {
                ChartObject counterpart;
                String order = input.substring(1).trim();
                try {
                    counterpart = findOnDesk(order);
                    if (counterpart instanceof Chart)
                        print(addChart(Mechanics.composite((Chart) chartObject, (Chart) counterpart)));
                } catch (ChartNotFoundException e) {
                    print("Карта '%s' не найдена: %s".formatted(order, e.getLocalizedMessage()));
                }

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

    @Override
    public boolean confirmationAnswer(String prompt) {
        printInFrame(prompt);
        while (true) {
            String answer = getUserInput();
            if (positiveAnswer(answer)) return true;
            if (negativeAnswer(answer)) return false;
            print("Введи да или нет, надо определить, третьего не дано.");
        }
    }

    public void editSettings() {
        showSettingsMenu();

        while (true) {
            String command = getUserInput();
            if (command.isBlank())
                break;
            int delimiter = command.indexOf("=");
            if (delimiter == -1) {
                print("Команда должна содержать оператор '='\n");
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
                    default -> print("Введи номер существующего параметра, а не '" + parameter + "'\n");
                }
            } catch (NumberFormatException e) {
                print("Не удалось прочитать значение.\n");
            }
        }
    }

    /**
     * Выводит на экран список карт, лежащих на {@link Application#DESK столе}, то есть загруженных в программу.
     */
    @Override
    public void displayDesk() {
        printInFrame(DESK.isEmpty() ?
                "Ни одной карты не загружено." :
                DESK.toString()
        );
    }


    @Override
    public void mainCycle() {
        displayDesk();
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
                case "1" -> displayDesk();
                case "2" -> editSettings();
                case "3" -> astroSource.listsCycle();
                case "4" -> workCycle(selectChartOnDesk());
                case "5" -> print(addChart(astroSource.getChartFromUserInput()));
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
        return KEYBOARD.nextLine().trim();
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>обновить – ставит новую карту на место старой карты с этим именем;</li>
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param nextChart добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в этом списке.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return {@code да}, если добавление карты (с переименованием либо с заменой) состоялось,
     *          {@code нет}, если была выбрана отмена.
     */
    @Override
    public boolean mergeResolving(ChartList list, ChartObject nextChart, String listName) {
        if (!list.contains(nextChart.getName())) {
            return list.addItem(nextChart);
        }
        while (true) {
            print("""
                                                    
                            Карта с именем '%s' уже есть %s:
                            1. добавить под новым именем
                            2. заменить присутствующую в списке
                            3. удалить старую, добавить новую в конец списка
                            0. отмена
                            """.formatted(nextChart.getName(),
                                            listName.startsWith("на ") ? listName : "в " + listName));
            switch (getUserInput()) {
                case "1" -> {
                    String rename;
                    do {
                        print("Новое имя: ");
                        rename = getUserInput();         // TODO: допустимое имя
                        print("\n");
                    } while (list.contains(rename));
                    nextChart.setName(rename);
                    return list.addItem(nextChart);
                }
                case "2" -> {
                    list.setItem(list.indexOf(nextChart.getName()), nextChart);
                    return true;
                }
                case "3" -> {
                    list.remove(nextChart.getName());
                    return list.addItem(nextChart);
                }
                case "0" -> {
                    print("Отмена добавления карты: " + nextChart.getName());
                    return false;
                }
            }
        }
    }
}
