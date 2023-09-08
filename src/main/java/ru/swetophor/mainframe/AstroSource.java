package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

public interface AstroSource {


    void listsCycle();

    /**
     * Прочитывает карты из файла в папке базы данных на {@link Application#DESK стол} {@link Application АстроВидьи}.
     *
     * @param filename имя файла в папке базы данных.
     */
    void loadFromFile(String filename);

    ChartObject getChartFromUserInput();

    ChartObject findChart(ChartList list, String order, String listDesc) throws ChartNotFoundException;

    boolean mergeChartIntoList(ChartList list, ChartObject nextChart, String listName);
}
