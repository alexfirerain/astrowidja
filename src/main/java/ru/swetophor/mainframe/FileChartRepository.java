package ru.swetophor.mainframe;

import static ru.swetophor.mainframe.Decorator.print;

public class FileChartRepository implements ChartRepository {
    @Override
    public void loadBase(String filename) {
        Storage.readChartsFromFile(filename)
                .forEach(c -> Application.DESK.addResolving(c, "на столе"));
        print("Загружены карты из " + filename);
    }
}
