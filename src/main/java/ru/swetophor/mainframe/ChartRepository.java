package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.List;

public interface ChartRepository {

    void saveTableToFile(ChartList table, String target);

    void loadBase(String filename);

    void autosave();

    ChartList findList(String order);

    void dropListToFile(ChartList list, String s);

    void putChartToBase(ChartObject chartObject, String baseName);

    ChartList readChartsFromBase(String baseName);

    List<ChartList> getBasesContent();

    List<String> baseNames();

    void deleteFile(String order);

//    ChartObject findChart(ChartList list, String order, String location) throws ChartNotFoundException;

    String listLibrary();
}
