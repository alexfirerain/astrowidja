package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.List;

public interface ChartRepository {

    String saveTableToFile(ChartList table, String target);

    String loadBase(String filename);

    String autosave();

    ChartList findList(String order);

    void dropListToFile(ChartList list, String s);

    void putChartToBase(ChartObject chartObject, String baseName);

    ChartList readChartsFromBase(String baseName);

    List<ChartList> getWholeLibrary();

    List<String> baseNames();

    String deleteFile(String order);

//    ChartObject findChart(ChartList list, String order, String location) throws ChartNotFoundException;

    String listLibrary();
}
