package ru.swetophor;


import ru.swetophor.celestialmechanics.*;
import ru.swetophor.celestialmechanics.Astra;

import static ru.swetophor.Settings.*;


/** ** ** ** ** ** ** ** ** ** ** ** ** ** **
 * Основной сценарий, описывающий работу
 * программы как таковой
 ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/
public class Application {
    public static void main(String[] args) {
        welcome();
//        Chart thisChart = new Chart("СЧ");
//        thisChart.addAstra(new Astra("Солнце", 283, 15, 49));
//        thisChart.addAstra(new Astra("Луна", 253, 6, 27));
//        thisChart.addAstra(new Astra("Венера", 260, 32, 34));
//        thisChart.addAstra(new Astra("Марс", 302, 58, 14));
//
        Chart SCChart = Chart.readFromString("""
                СЧ
                Солнце 283 15 49
                Луна 253 6 27
                Венера 260 32 34
                Марс 302 58 14
                """);

        System.out.println(SCChart.printAstrasList());
        SCChart.plotAspectTable();

        Chart moreChart = new Chart("Сева");
        moreChart.addAstra(new Astra("Солнце", 81, 5, 8));
        moreChart.addAstra(new Astra("Луна", 348, 59, 25));
        moreChart.addAstra(new Astra("Венера", 35, 19, 47));
        moreChart.addAstra(new Astra("Марс", 143, 35, 4));
        System.out.println(moreChart.printAstrasList());
        moreChart.plotAspectTable();

        Synastry doubleChart = new Synastry(SCChart, moreChart);
        doubleChart.plotAspectTable();
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
