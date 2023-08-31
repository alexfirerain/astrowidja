package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.util.List;

public interface ChartRepository {

    void saveTableToFile(ChartList table, String target);

    void loadBase(String filename);

    void autosave();

    ChartList findList(String order);

    void dropListToFile(ChartList desk, String s);

    void putChartToBase(ChartObject chartObject, String baseName);

    ChartList readChartsFromBase(String baseName);

    List<ChartList> scanLibrary();

    List<String> tableOfContents();

    void deleteFile(String order);
}
