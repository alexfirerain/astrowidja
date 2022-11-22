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
        Chart thisChart = new Chart("СЧ");
        thisChart.addAstra(new Astra("Солнце", 283, 15, 49));
        thisChart.addAstra(new Astra("Луна", 253, 6, 27));
        thisChart.addAstra(new Astra("Венера", 260, 32, 34));
        thisChart.addAstra(new Astra("Марс", 302, 58, 14));
        thisChart.printAstrasList();
        thisChart.plotAspectTable();
        Chart moreChart = new Chart("Сева");
        moreChart.addAstra(new Astra("Солнце", 81, 5, 8));
        moreChart.addAstra(new Astra("Луна", 348, 59, 25));
        moreChart.addAstra(new Astra("Венера", 35, 19, 47));
        moreChart.addAstra(new Astra("Марс", 143, 35, 4));
        moreChart.printAstrasList();
        moreChart.plotAspectTable();
        Synastry двойная = new Synastry(thisChart, moreChart);
        двойная.plotAspectTable();
    }
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
