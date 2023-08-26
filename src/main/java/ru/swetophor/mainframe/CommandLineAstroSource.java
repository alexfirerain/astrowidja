package ru.swetophor.mainframe;

import static ru.swetophor.mainframe.Decorator.print;

public class CommandLineAstroSource implements AstroSource {


    @Override
    public void loadFromFile(String filename) {
        Storage.readChartsFromFile(filename)
                .forEach(c -> Application.DESK.addResolving(c, "на столе"));
        print("Загружены карты из " + filename);
    }
}
