package ru.swetophor;


import ru.swetophor.celestialmechanics.*;

import static ru.swetophor.Settings.*;


/** ** ** ** ** ** ** ** ** ** ** ** ** ** **
 * Основной сценарий, описывающий работу
 * программы как таковой
 ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/
public class Application {
    public static void main(String[] args) {
        welcome();

        Chart SCChart = Chart.readFromString("""
                СЧ
                Солнце 283 15 49
                Луна 253 6 27
                Венера 260 32 34
                Марс 302 58 14
                Юпитер 189 41
                Сатурн 189 35
                Уран 238 33
                Нептун 263 9
                Плутон 204 11
                Хирон 43 34
                Раху 131 8
                """);
        printChartStat(SCChart);

        Chart SWChart = Chart.readFromString("""
                Сева
                Солнце 81 5 8
                Луна 348 59 25
                Венера 35 19 47
                Марс 143 35 4
                """);
        printChartStat(SWChart);

        Synastry doubleChart = new Synastry(SCChart, SWChart);
        doubleChart.plotAspectTable();
    }

    private static void printChartStat(Chart chart) {
        System.out.println(chart.getAstrasList());
        System.out.println(chart.getAspectTable());
    }

    // TODO: вынести процедуру загрузки астр, запариться с чтением из файла.

    static public int IDs = 0;

    private static void welcome() {
        System.out.printf("""
        *********************************
        * Начато исполнение АстроВидьи! *
        *********************************
        Считаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n
        """,
                getOrbs(), orbsDivider, edgeHarmonic);
    }
    public static void disableHalfOrbisForDoubles() { halfOrbsForDoubles = false; }
    public static void enableHalfOrbisForDoubles() { halfOrbsForDoubles = true; }

}
