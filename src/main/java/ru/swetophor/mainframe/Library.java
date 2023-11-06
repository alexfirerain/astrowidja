package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Library {
    private final List<String> groupNames;
    private final List<ChartList> chartCatalogue;


    public Library() {
        groupNames = new ArrayList<>();
        chartCatalogue = new ArrayList<>();
    }

    public void addGroup(String groupName, ChartList chartNames) {
        groupNames.add(groupName);
        chartCatalogue.add(chartNames);
    }

    public ChartList getGroupByName(String groupName) {
        int groupIndex = groupNames.indexOf(groupName);
        return groupIndex != -1 ?
                chartCatalogue.get(groupIndex) : null;
    }

    public ChartList getGroupByNumber(int index) {
        return index > -1 && index < chartCatalogue.size()  ?
                chartCatalogue.get(index) : null;
    }

    public ChartObject getByGroupAndChartName(String groupName, String chartName) {
        if (groupName == null || groupName.isBlank())
            throw new IllegalArgumentException("Список не указан.");
        if (chartName == null || chartName.isBlank())
            throw new IllegalArgumentException("Карта не указана.");

        ChartList list;

        if (groupName.matches("^\\d+")) {
            try {
                int i = Integer.parseInt(groupName) - 1;
                if (i >= 0 && i < chartCatalogue.size()) {
                        list = chartCatalogue.get(i);
                } else {
                    throw new IllegalArgumentException("В базе всего %d файлов%n"
                            .formatted(chartCatalogue.size()));
                }
        } catch (NumberFormatException e) {
            System.out.println("Число не распознано.");
        }
        try {
        }
        else {
            int index;
            List<String> tableOfContents = baseNames();
            index = IntStream.range(0, tableOfContents.size())
                    .filter(i -> tableOfContents.get(i).startsWith(order))
                    .findFirst()
                    .orElse(-1);
            if (index == -1) {
                System.out.println("Нет файла с именем на " + order);
            }
            else {
                list = base.get(index);
            }
        }


        int groupIndex = groupNames.indexOf(groupName);
        if (groupIndex == -1) {
            groupIndex = IntStream.range(0, groupNames.size())
                    .filter(i -> groupNames.get(i).startsWith(groupName))
                    .findFirst().orElseThrow(() ->
                            new IllegalArgumentException("Списка карт %s не обнаружено.".formatted(groupName)));
        }
        list = chartCatalogue.get(groupIndex);

        return list.get(list.indexOf(chartName));
    }

    public void updateLibrary(ChartRepository chartRepository) {
        groupNames.clear();
        groupNames.addAll(chartRepository.baseNames());
        chartCatalogue.clear();
        chartCatalogue.addAll(chartRepository.getWholeLibrary());
    }



}
