package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.*;

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


    public static ChartObject findOnDesk(String order) throws ChartNotFoundException {
        return findChart(DESK, order, "на столе");
    }

    /**
     * Находит в этом списке карту, заданную по имени или номеру в списке (начинающемуся с 1).
     * Если запрос состоит только из цифр, рассматривает его как запрос по номеру,
     * иначе как запрос по имени.
     * @param order запрос, какую карту ищем в списке: по имени или номеру (с 1).
     * @param inList    строка, описывающая этот список в местном падеже.
     * @return  найденный в списке объект, соответствующий запросу.
     * @throws ChartNotFoundException   если в списке не найдено соответствующих запросу объектов.
     */
    public static ChartObject findChart(ChartList list, String order, String inList) throws ChartNotFoundException {
        if (order == null || order.isBlank())
            throw new ChartNotFoundException("Пустой запрос.");
        if (!inList.startsWith("на "))
            inList = "в " + inList;

        if (order.matches("^\\d+"))
            try {
                int i = Integer.parseInt(order) - 1;
                if (i >= 0 && i < list.size())
                    return list.get(i);
                else
                    throw new ChartNotFoundException("Всего %d карт %s%n"
                            .formatted(list.size(), inList));
            } catch (NumberFormatException e) {
                throw new ChartNotFoundException("Число не распознано.");
            }
        else if (list.contains(order)) {
            return list.get(order);
        } else {
            for (String name : list.getNames())
                if (name.startsWith(order))
                    return list.get(name);

            throw new ChartNotFoundException("Карты '%s' нет %s%n"
                    .formatted(order, inList));
        }
    }
}
