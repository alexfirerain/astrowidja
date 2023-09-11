package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;

public interface AstroSource {




    /**
     * Прочитывает карты из файла в папке базы данных на {@link Application#DESK стол} {@link Application АстроВидьи}.
     *
     * @param filename имя файла в папке базы данных.
     */
    void loadFromFile(String filename);

    Chart getChartFromUserInput(String data);

    ChartObject findChart(ChartList list, String order, String listDesc) throws ChartNotFoundException;

    boolean mergeChartIntoList(ChartList list, ChartObject nextChart, String listName);
}
