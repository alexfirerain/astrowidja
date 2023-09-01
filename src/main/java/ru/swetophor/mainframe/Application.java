package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.*;

import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Settings.*;


/**
 * Основной сценарий, описывающий работу
 * программы как таковой
 */
public class Application {
    /**
     * Счётчик для нумерации объектов типа КартОбъект.
     */
    @Deprecated
    static public int id = 0;

    /**
     * Главное меню, с которого начинается работа с программой.
     */
    static final MainUI mainShield;
    static final AstroSource astroSource;
    static final ChartRepository chartRepository;


    /*
        Инициализация имплементации
     */
    static {
        mainShield = new CommandLineMainUI();
        astroSource = new CommandLineAstroSource();
        chartRepository = new FileChartRepository();
    }

    /**
     * Статический список карт в памяти работающей АстроВидьи.
     */
    protected static final ChartList DESK = new ChartList();

    public final AstroSet DEFAULT_ASTRO_SET = new AstroSet(AstraEntity.values());


    private static String getChartStat(ChartObject chart) {
        return chart.getAstrasList() +
                chart.getAspectTable() +
                chart.resonanceAnalysisVerbose(Settings.getEdgeHarmonic());
    }


    public static void main(String[] args) {

        mainShield.welcome();

        if (autoloadEnabled)
            chartRepository.loadBase("autosave.awb");

        mainShield.mainCycle();

        if (autosave)
            chartRepository.autosave();
    }


}
