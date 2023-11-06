package ru.swetophor.mainframe;

import java.util.ArrayList;
import java.util.List;

public class LibraryService {
    private final ChartRepository chartRepository;

    public LibraryService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    static Library library = new Library();

    private void updateLibrary() {
        library.updateLibrary(chartRepository);
    }

    public ChartList findList(String chartListOrder) {
        return chartRepository.findList(chartListOrder);
    }
}
