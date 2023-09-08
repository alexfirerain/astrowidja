package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;
import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;

import java.util.List;

import static ru.swetophor.mainframe.Application.*;
import static ru.swetophor.mainframe.CommandLineMainUI.KEYBOARD;
import static ru.swetophor.mainframe.Decorator.*;

public class CommandLineAstroSource implements AstroSource {


    static String extractHeadOrder(String input) {
        return input.substring(0, input.length() - 2).trim();
    }

    static String extractTailOrder(String input) {
        return input.substring(2).trim();
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return {@code true}, если карта (с оригинальным либо обновлённым именем) добавлена в список.
     */
    public static boolean mergeResolving(ChartObject controversial, List<ChartObject> list, String listName) {
        while (true) {
            System.out.printf("""
                                                    
                            Карта с именем '%s' уже есть:
                            1. заменить присутствующую %s
                            2. добавить под новым именем
                            0. отмена
                            """, controversial.getName(),
                    listName.startsWith("на ") ?
                            listName :
                            "в " + listName);
            switch (mainShield.getUserInput()) {
                case "1" -> {
                    list.stream()
                            .filter(c -> c.getName()
                                    .equals(controversial.getName()))
                            .findFirst()
                            .ifPresent(list::remove);
                    return list.add(controversial);
                }
                case "2" -> {
                    String name;
                    do {
                        System.out.print("Новое имя: ");
                        name = mainShield.getUserInput();         // TODO: допустимое имя
                        System.out.println();
                    } while (ChartList.containsName(list, name));
                    controversial.setName(name);
                    return list.add(controversial);
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return false;
                }
            }
        }
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     */
    public static void mergeResolving(ChartObject controversial, ChartList list, String listName) {
        while (true) {
            System.out.printf("""
                                                    
                            Карта с именем '%s' уже есть:
                            1. заменить присутствующую %s
                            2. добавить под новым именем
                            0. отмена
                            """, controversial.getName(),
                    listName.startsWith("на ") ?
                            listName :
                            "в " + listName);
            switch (mainShield.getUserInput()) {
                case "1" -> {
                    list.remove(controversial.getName());
                    list.addItem(controversial);
                    return;
                }
                case "2" -> {
                    String rename;
                    do {
                        System.out.print("Новое имя: ");
                        rename = mainShield.getUserInput();         // TODO: допустимое имя
                        System.out.println();
                    } while (list.contains(rename));
                    controversial.setName(rename);
                    list.addItem(controversial);
                    return;
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return;
                }
            }
        }
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, возвращает добавляемую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты, обновляет её и возвращает;</li>
     * <li>отмена – возвращает {@code null}</li>
     * <p>
     * Таким образом, возвращаемое функцией значение соответствует той карте, которую
     * следует следующим шагом добавить в целевой список.
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return ту же карту, если выбрано "заменить старую карту",
     * ту же карту с новым именем, если выбрано "переименовать новую карту",
     * или {@code пусто}, если выбрано "отменить операцию".
     */
    public static ChartObject resolveCollision(ChartObject controversial, List<ChartObject> list, String listName) {
        ChartObject result = controversial;
        boolean fixed = false;
        while (!fixed) {
            System.out.printf("""
                                            
                    Карта с именем %s уже есть:
                    1. заменить присутствующую в %s
                    2. добавить под новым именем
                    0. отмена
                    """, controversial.getName(), listName);
            switch (mainShield.getUserInput()) {
                case "1" -> {
                    list.stream()
                            .filter(c -> c.getName()
                                    .equals(controversial.getName()))
                            .findFirst()
                            .ifPresent(list::remove);
                    fixed = true;
                }
                case "2" -> {
                    String name;
                    do {
                        System.out.print("Новое имя: ");
                        name = mainShield.getUserInput();         // TODO: допустимое имя
                        System.out.println();
                    } while (ChartList.containsName(list, name));
                    result.setName(name);
                    fixed = true;
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    result = null;
                    fixed = true;
                }
            }
        }
        return controversial;
    }

    public static List<ChartObject> mergeList(List<ChartObject> addingCharts, List<ChartObject> mergingList, String listName) {
        if (addingCharts == null || addingCharts.isEmpty())
            return mergingList;
        if (mergingList == null || mergingList.isEmpty())
            return addingCharts;
        for (ChartObject adding : addingCharts)
            if (ChartList.containsName(mergingList, adding.getName()))
                mergeResolving(adding, mergingList, listName);
            else
                mergingList.add(adding);
        return mergingList;
    }

    /**
     * Вливает в старый список содержимое нового списка.
     * Если какая-то из добавляемых карт имеет имя, которое уже содержится в списке,
     * запускается интерактивная процедура разрешения коллизии.
     *
     * @param addingCharts список добавляемых карт.
     * @param mergingList  список карт, в который добавляется.
     * @param listName     имя списка, в который добавляется, в предложном падеже
     *                     (с предлогом "на", если он предпочтительнее предлога "в").
     * @return старый список, в который добавлены карты из нового списка, и из которого,
     * если пользователем выбирался такой вариант, удалены старые карты с совпавшими именами.
     */
    public static ChartList mergeList(ChartList addingCharts, ChartList mergingList, String listName) {
        if (addingCharts == null || addingCharts.isEmpty())
            return mergingList;
        if (mergingList == null || mergingList.isEmpty())
            return addingCharts;
        for (ChartObject adding : addingCharts.getCharts())
                mergingList.addResolving(adding, listName);
        return mergingList;
    }

    /**
     * Находит в этом списке карту, заданную по имени или номеру в списке (начинающемуся с 1).
     * Если запрос состоит только из цифр, рассматривает его как запрос по номеру,
     * иначе как запрос по имени.
     * @param order запрос, какую карту ищем в списке: по имени или номеру (с 1).
     * @param inList    строка, описывающая этот список в местном падеже.
     * @return  найденный в списке объект, соответствующий запросу.
     * @throws ChartNotFoundException   если в списке не найдено соответствующих запросу объектов.
     */
    @Override
    public ChartObject findChart(ChartList list, String order, String inList) throws ChartNotFoundException {
        if (order == null || order.isBlank())
            throw new ChartNotFoundException("Пустой запрос.");
        if (!inList.startsWith("на "))
            inList = "в " + inList;

        if (order.matches("^\\d+"))
            try {
                int i = Integer.parseInt(order) - 1;
                if (i >= 0 && i < list.size())
                    return list.get(i);
                else
                    throw new ChartNotFoundException("Всего %d карт %s%n"
                            .formatted(list.size(), inList));
            } catch (NumberFormatException e) {
                throw new ChartNotFoundException("Число не распознано.");
            }
        else if (list.contains(order)) {
            return list.get(order);
        } else {
            for (String name : list.getNames())
                if (name.startsWith(order))
                    return list.get(name);

            throw new ChartNotFoundException("Карты '%s' нет %s%n"
                    .formatted(order, inList));
        }
    }

    /**
     * Создаёт карту на основе юзерского ввода.
     * Предлагает ввести координаты в виде "градусы минуты секунды"
     * для каждой стандартной {@link AstraEntity АстроСущности}. Затем предлагает вводить
     * дополнительные {@link Astra астры} в виде "название градусы минуты секунды".
     * Пустой ввод означает пропуск астры или отказ от дополнительного ввода.
     *
     * @return {@link Chart одиночную карту}, созданную на основе ввода.
     */
    @Override
    public Chart getChartFromUserInput() {
        print("Название новой карты: ");
        Chart x = new Chart(mainShield.getUserInput());
        for (AstraEntity a : AstraEntity.values()) {
            print(a.name + ": ");
            String input = mainShield.getUserInput();
            if (input.isBlank())
                continue;
            x.addAstraFromString(a.name + " " + input);
            print();
        }
        print("Ввод дополнительных астр в формате 'название градусы минуты секунды'");
        String input = mainShield.getUserInput();
        while (!input.isBlank()) {
            x.addAstraFromString(input);
            input = mainShield.getUserInput();
        }
        return x;
    }

    @Override
    public void listsCycle() {
        String LIST_MENU = """
                ("список" — список по номеру или имени,
                 "карты" — карты по номеру или имени через пробел)
                    =               = список файлов в базе
                    ==              = полный список файлов и карт
                    ххх список      = удалить файл
                    
                    список >>       = заменить стол на список
                    список ->       = добавить список ко столу
                    >> список       = заменить файл столом
                    -> список       = добавить стол к списку
                    
                    карты -> список         = добавить карты со стола к списку
                    список:карты -> список  = переместить карты из списка в список
                    список:карты +> список  = копировать карты из списка в список
                """;
        printInSemiDouble(LIST_MENU);
        String input;
        while (true) {
            input = mainShield.getUserInput();
            if (input == null || input.isBlank()) return;

            if (input.equals("=")) {
                printInAsterisk(chartRepository.listLibrary());

            } else if (input.equals("==")) {
                printInAsterisk(FileChartRepository.reportBaseContentExpanded());

            } else if (input.toLowerCase().startsWith("xxx") || input.toLowerCase().startsWith("ххх")) {
                String order = input.substring(3).trim();
                chartRepository.deleteFile(order);

            } else if (input.endsWith(">>")) {
                ChartList loadingList = chartRepository.findList(extractHeadOrder(input));
                if (loadingList == null || loadingList.isEmpty()) {     // TODO: write a confirmation general utility
                    System.out.println("список не найден или пуст");
                } else {
                    DESK.clear();
                    DESK.addAll(loadingList);
                    mainShield.displayDesk();
                }

            } else if (input.endsWith("->")) {
                DESK.addAll(chartRepository.findList(extractHeadOrder(input)));
                mainShield.displayDesk();

            } else if (input.startsWith(">>")) {
                chartRepository.dropListToFile(DESK, extractTailOrder(input));

            } else if (input.startsWith("->")) {
                chartRepository.saveTableToFile(DESK, extractTailOrder(input));
            }
        }

    }

    @Override
    public void loadFromFile(String filename) {
        chartRepository.readChartsFromBase(filename)
                .forEach(c -> Application.DESK.addResolving(c, "на столе"));
        print("Загружены карты из " + filename);
    }

    /**
     * Добавляет карту к списку. Если имя добавляемой карты в нём уже содержится,
     * запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>переименовать – запрашивает новое имя для добавляемой карты и добавляет обновлённую;</li>
     * <li>обновить – ставит новую карту на место старой карты с этим именем;</li>
     * <li>заменить – удаляет из списка карту с конфликтным именем, добавляет новую;</li>
     * <li>отмена – карта не добавляется.</li>
     *
     * @param nextChart добавляемая карта.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return {@code ДА}, если добавление карты (с переименованием либо с заменой) состоялось,
     *          или {@code НЕТ}, если была выбрана отмена.
     */
    @Override
    public boolean mergeChartIntoList(ChartList list, ChartObject nextChart, String listName) {
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
            switch (mainShield.getUserInput()) {
                case "1" -> {
                    String rename;
                    do {
                        print("Новое имя: ");
                        rename = mainShield.getUserInput();         // TODO: допустимое имя
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
