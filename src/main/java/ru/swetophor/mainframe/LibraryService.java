package ru.swetophor.mainframe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return library.findList(chartListOrder);
    }

    public String listLibrary() {
        List<String> names = library.getGroupNames();
        return IntStream.range(0, names.size())
                .mapToObj(i -> "%d. %s%n"
                        .formatted(i + 1, names.get(i)))
                .collect(Collectors.joining());
    }
}
