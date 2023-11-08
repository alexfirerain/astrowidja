package ru.swetophor.mainframe;

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

    public String exploreLibrary() {
        return library.exploreLibrary();
    }

    public String listLibrary() {
        return library.listLibrary();
    }
}
