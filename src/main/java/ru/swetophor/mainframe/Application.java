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
    static final MainGUI mainShield;
    static final AstroSource astroSource;


    /*
        Инициализация имплементации
     */
    static {
        mainShield = new CommandLineMainGUI();
        astroSource = new CommandLineAstroSource();
    }

    /**
     * Статический список карт в памяти работающей АстроВидьи.
     */
    protected static final ChartList DESK = new ChartList();

    public final AstroSet DEFAULT_ASTRO_SET = new AstroSet(AstraEntity.values());


    private static void printChartStat(ChartObject chart) {
        print(chart.getAstrasList());
        print(chart.getAspectTable());
        print(chart.resonanceAnalysisVerbose(Settings.getEdgeHarmonic()));
    }


    public static void main(String[] args) {

        mainShield.welcome();

        if (autoloadEnabled)
            astroSource.loadFromFile("autosave.awb");

        mainShield.mainCycle();

        if (autosave)
            Storage.saveTableToFile(DESK, Storage.autosaveName());
    }

    /**
     * Запрашивает карту по имени или номеру на столе,
     * если она найдена, выводит её статистику на экран.
     */
    private static void showChart() {
        System.out.print("Укажите карту по номеру на столе или по имени: ");
        String order = mainShield.getUserInput();
        if (order.isBlank())
            return;
        if (order.matches("^\\d+"))
            try {
                int i = Integer.parseInt(order) - 1;
                ChartObject result = DESK.get(i);
                if (result != null)
                    printChartStat(result);
                else
                    System.out.println("Карты под номером " + order + " не найдено.");
            } catch (NumberFormatException e) {
                System.out.println("Число не распознано.");
            }
        else if (DESK.contains(order))
            printChartStat(DESK.get(order));
        else
            System.out.println("Карты с именем " + order + " не найдено.");
    }

    /**
     * Запрашивает, какую карту со {@link #DESK стола} взять в работу,
     * т.е. запустить в {@link CommandLineMainGUI#workCycle(ChartObject) цикле процедур для карты}.
     * Если карта не опознана по номеру на столе или имени, не делает ничего.
     * Но функция поиска сама выводит сообщения, если не нашла.
     */
    public static void takeChart() {
        System.out.print("Укажите карту по имени или номеру на столе: ");
        String order = mainShield.getUserInput();
        ChartObject taken = DESK.findChart(order, "на столе");
        if (taken == null) return;
        CommandLineMainGUI.workCycle(taken);
    }

    /**
     * Добавляет карту на {@link #DESK стол}. Если карта с таким именем уже
     * присутствует, запрашивает решение у юзера.
     *
     * @param chart добавляемая карта.
     */
    static void addChart(ChartObject chart) {
        if (DESK.addResolving(chart, "на столе"))
            print("Карта загружена на стол: " + chart);
    }

    /**
     * Добавляет {@link #DESK в реестр} произвольное количество карт из аргументов.
     * Если какая-то карта совпадает с уже записанной, у юзера
     * запрашивается решение.
     * @param charts добавляемые карты.
     */
    private static void addChart(ChartObject... charts) {
        for (ChartObject chart : charts)
            addChart(chart);
    }


}
