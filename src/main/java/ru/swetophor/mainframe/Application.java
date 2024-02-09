package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.*;

import static ru.swetophor.mainframe.Decorator.print;
import static ru.swetophor.mainframe.Settings.*;


/**
 * Основной сценарий, описывающий работу
 * программы как таковой
 */
public class Application {
    /**
     * Счётчик для нумерации объектов типа КартОбъект.
     */
//    @Deprecated
    static public int id = 0;

    /**
     * Главное меню, с которого начинается работа с программой.
     */
    static final MainUI mainShield;
    static final AstroSource astroSource;
    static final LibraryService libraryService;
    static final ChartRepository chartRepository;


    /*
        Инициализация имплементации
     */
    static {
        astroSource = new CommandLineAstroSource();
        chartRepository = new FileChartRepository();
        libraryService = new LibraryService(chartRepository);
        mainShield = new CommandLineMainUI(libraryService);
    }

    /**
     * Статический список карт в памяти работающей АстроВидьи.
     */
    protected static final ChartList DESK = new ChartList("Стол Астровидьи");

    public static final AstroSet DEFAULT_ASTRO_SET = new AstroSet(AstraEntity.values());


    private static String getChartStat(ChartObject chart) {
        return chart.getAstrasList() +
                chart.getAspectTable() +
                chart.resonanceAnalysisVerbose(Settings.getEdgeHarmonic());
    }


    public static void main(String[] args) {

        mainShield.welcome();

        if (isAutoloadEnabled())
            print(chartRepository.loadBase(getAutoloadFile()));

        mainShield.mainCycle();

        if (isAutosave())
            print(chartRepository.autosave());
    }


    public static ChartObject findOnDesk(String order) throws ChartNotFoundException {
        return astroSource.findChart(DESK, order, "на столе");
    }

}
