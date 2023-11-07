package ru.swetophor.mainframe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.swetophor.celestialmechanics.ChartObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class Library {
    private final List<String> groupNames = new ArrayList<>();
    private final List<ChartList> chartCatalogue = new ArrayList<>();


    public void addGroup(String groupName, ChartList chartNames) {
        groupNames.add(groupName);
        chartCatalogue.add(chartNames);
    }

    public ChartObject getByGroupAndChartName(String groupName, String chartName) {
        ChartAddress order = defineAddress(groupName, chartName);
        return chartCatalogue
                        .get(order.getGroupIndex())
                        .get(order.getChartIndex());
    }

    public void updateLibrary(ChartRepository chartRepository) {
        groupNames.clear();
        groupNames.addAll(chartRepository.baseNames());
        chartCatalogue.clear();
        chartCatalogue.addAll(chartRepository.getWholeLibrary());
    }

    /**
     * Определяет индекс строкового элемента в списке, указанного через номер или имя.
     * Если ввод состоит только из цифр, трактует его как десятичное значение индекса,
     * (нумерация с единицы). В ином случае ищет сначала индекс точного совпадения
     * ввода с элементом, затем, если его не найдено, индекс первого элемента с совпадением
     * начальных символов.
     * @param input строка, анализируемая как источник индекса.
     * @param list  список строк, индекс в котором ищется.
     * @return  индекс элемента в списке, если ввод содержит корректный номер или
     * присутствующий элемент.
     * @throws IllegalArgumentException если ни по номеру, ни по имени элемента
     * не найдено в списке, а также если аргументы пусты.
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
                throw new IllegalArgumentException("Элемент %d не найден: всего %d элементов."
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
     * @param groupInput    текстовый ввод для определения группы.
     * @param chartNameInput    текстовый ввод для определения карты.
     * @return  объект-контейнер, содержащий два числа,
     * определяющие место карты в Библиотеке (индекс группы и индекс карты).
     * @throws IllegalArgumentException если ввод не распознан как адрес
     * существующей в Библиотеке карты.
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

    public ChartList findList(String chartListOrder) {
        int groupIndex;
        try {
            groupIndex = defineIndexFromInput(chartListOrder, groupNames);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Списка не найдено: " + e);
        }
        return chartCatalogue.get(groupIndex);
    }

    @AllArgsConstructor
    @Getter
    private static class ChartAddress {
        private int groupIndex;
        private int chartIndex;
    }

}
