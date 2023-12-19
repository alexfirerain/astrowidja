package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LibraryService {
    /**
     * Реализация картохранилища, хранящего список списков карт.
     */
    private final ChartRepository chartRepository;
    /**
     * Имена актуальных списков (групп) карт.
     */
    private final List<String> groupNames = new ArrayList<>();
    /**
     * Отображение в памяти базы данных карт, группированных по спискам.
     */
    private final List<ChartList> chartCatalogue = new ArrayList<>();


    public LibraryService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
        rereadLibrary();
    }

    /**
     * Очищает отображение структуры библиотеки в памяти (имена групп
     * и списки карт) и заново записывает его на основании данных от {@link #chartRepository картохранилища}.
     * Процедура должна выполняться при инициализации сервиса и
     * в конце всякого модифицирующего обращения к картохранилщу.
     */
    public void rereadLibrary() {
        groupNames.clear();
        groupNames.addAll(chartRepository.baseNames());
        chartCatalogue.clear();
        chartCatalogue.addAll(chartRepository.getWholeLibrary());
    }

    /**
     * Определяет индекс (от 0) строкового элемента в списке,
     * указанного через номер (от 1) или имя.
     * Если ввод состоит только из цифр, трактует его как десятичное значение индекса,
     * (нумерация с единицы).
     * В ином случае ищет сначала индекс точного совпадения
     * ввода с элементом, затем, если его не найдено, индекс первого элемента с совпадением
     * начальных символов.
     *
     * @param input строка, анализируемая как источник индекса.
     * @param list  список строк, индекс в котором ищется.
     * @return индекс элемента в списке, если ввод содержит корректный номер или
     * присутствующий элемент.
     * @throws IllegalArgumentException если ни по номеру, ни по имени элемента
     *                                  не найдено в списке, а также если аргументы пусты.
     */
    private int defineIndexFromInput(String input, List<String> list) {
        if (input == null || input.isBlank())
            throw new IllegalArgumentException("Элемент не указан.");
        if (list == null || list.isEmpty())
            throw new IllegalArgumentException("Список не указан.");
        int index;
        if (input.matches("^[0-9]+$")) {
            try {
                index = Integer.parseInt(input) - 1;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ошибка распознавания числа.");
            }
            if (index < 0 || index >= list.size())
                throw new IllegalArgumentException("Элемент %d отсутствует: всего %d элементов."
                        .formatted(index + 1, list.size()));
        } else {
            index = list.indexOf(input);
            if (index == -1)
                index = IntStream.range(0, list.size())
                        .filter(i -> list.get(i).startsWith(input))
                        .findFirst().orElseThrow(() ->
                                new IllegalArgumentException("Не обнаружено элементов, начинающихся с %s"
                                        .formatted(input)));
        }
        return index;
    }

    /**
     * Возвращает объект, содержащий индекс группы в Библиотеке
     * и индекс элемента в этой группе.
     *
     * @param groupInput     текстовый ввод для определения группы.
     * @param chartNameInput текстовый ввод для определения карты.
     * @return объект-контейнер, содержащий два числа,
     * определяющие место карты в Библиотеке (индекс группы и индекс карты).
     * @throws IllegalArgumentException если ввод не распознан как адрес
     *                                  существующей в Библиотеке карты.
     */
    private ChartAddress defineAddress(String groupInput, String chartNameInput) {
        int groupIndex;
        try {
            groupIndex = defineIndexFromInput(groupInput, groupNames);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Списка не найдено: " + e);
        }
        int chartIndex;
        try {
            chartIndex = defineIndexFromInput(chartNameInput, chartCatalogue.get(groupIndex).getNames());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Карты не найдено: " + e);
        }
        return new ChartAddress(groupIndex, chartIndex);
    }

    /**
     * Находит в базе (ея отображении в памяти) список карт по
     * строке ввода.
     *
     * @param chartListOrder строка ввода, как номер, название
     *                       или первые символы названия.
     * @return список карт с указанным номером или названием.
     * @throws IllegalArgumentException если по вводу не опознан список.
     */
    public ChartList findList(String chartListOrder) {
        int groupIndex;
        try {
            groupIndex = defineIndexFromInput(chartListOrder, groupNames);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Списка не найдено: " + e);
        }
        return chartCatalogue.get(groupIndex);
    }

    /**
     * Выдаёт строковое представление содержимого библиотеки
     * (как оно отображается в памяти).
     *
     * @return нумерованный (с 1) список групп, вслед каждой группе -
     * нумерованный (с 1) список карт в ней.
     */
    public String libraryListing() {
        StringBuilder output = new StringBuilder();
        IntStream.range(0, groupNames.size())
                .forEach(g -> {
                    output.append("%d. %s:%n"
                            .formatted(g + 1, groupNames.get(g)));
                    output.append(chartCatalogue.get(g).toString()
                            .lines().map(l -> "\t" + l)
                            .collect(Collectors.joining("\n")));
                });
        return output.toString();
    }

    /**
     * Выдаёт строковое представление групп карт в библиотеке.
     *
     * @return нумерованный (с 1) список групп.
     */
    public String listLibrary() {
        return IntStream.range(0, groupNames.size())
                .mapToObj(i -> "%d. %s%n"
                        .formatted(i + 1, groupNames.get(i)))
                .collect(Collectors.joining());
    }

    public String saveToNewGroup(String groupName, ChartList chartList) {
        String savingResult = chartRepository.addChartsToGroup(chartList, groupName);

        groupNames.add(groupName);
        chartCatalogue.add(chartList); // TODO: выбрать одно из двух обновлений каталога в памяти

        rereadLibrary();
        return savingResult;
    }

    public ChartObject getByGroupAndChartName(String groupName, String chartName) {
        var order = defineAddress(groupName, chartName);
        return chartCatalogue
                .get(order.groupIndex())
                .get(order.chartIndex());
    }

}
