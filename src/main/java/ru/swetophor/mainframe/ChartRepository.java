package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.List;

public interface ChartRepository {

    String addChartsToGroup(ChartList table, String target);

    String loadBase(String filename);

    String autosave();

    ChartList findList(String order);

    void saveChartsAsGroup(ChartList list, String s);

    boolean putChartsToBase(String baseName, ChartObject... chartObject);

    ChartList readChartsFromBase(String baseName);

    List<ChartList> getWholeLibrary();

    List<String> baseNames();

    String deleteFile(String order);

//    ChartObject findChart(ChartList list, String order, String location) throws ChartNotFoundException;

    String listLibrary();
}
