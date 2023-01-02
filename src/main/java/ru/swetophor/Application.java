package ru.swetophor;


import ru.swetophor.celestialmechanics.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ru.swetophor.Settings.*;


/** ** ** ** ** ** ** ** ** ** ** ** ** ** **
 * Основной сценарий, описывающий работу
 * программы как таковой
 ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/
public class Application {
private static final Scanner keyboard = new Scanner(System.in);

    static String SC = """
                СЧ
                Солнце 283 15 49
                Луна 253 6 27
                Меркурий 285 13 59
                Венера 260 32 34
                Марс 302 58 14
                Церера 112 17 23
                Юпитер 189 41 40
                Сатурн 189 34 56
                Хирон 43 35 4
                Уран 238 32 43
                Нептун 263 9 4
                Плутон 204 11 20
                Раху 132 22 11
                Лилит 202 7 59
                """;

    static String TV = """
            Таня
            Солнце 178 14 6
            Луна 206 41 44
            Меркурий 160 50 49
            Венера 167 31 18
            Марс 69 0 55
            Церера 161 53 58
            Юпитер 126 42 29
            Сатурн 288 42 24
            Хирон 115 37 11
            Уран 275 37 10
            Нептун 281 47 57
            Плутон 225 52 18
            Раху 304 30 35
            Лилит 251 45 47
            """;

    static String SW = """
                Сева
                Солнце 81 5 8
                Луна 348 59 25
                Венера 35 19 47
                Марс 143 35 4
                """;
    public static void main(String[] args) {
        welcome();

        Chart SCChart = Chart.readFromString(SC);
        printChartStat(SCChart);

        Chart TVChart = Chart.readFromString(TV);
        printChartStat(TVChart);

        Synastry doubleChart = new Synastry(SCChart, TVChart);
        doubleChart.plotAspectTable();

        Chart SCTVComposite = Chart.composite(SCChart, TVChart);
        printChartStat(SCTVComposite);
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

    static private final Map<String, ChartObject> table = new HashMap<>();

    private static final String menu = """
            1. загруженные карты
            2. настройки
            3. управление картами
            4. ввод новых данных
            """;

    private static Chart enterChartData() {
        System.out.print("Название новой карты: ");
        Chart x = new Chart(keyboard.nextLine());
        for (AstraEntity a : AstraEntity.values()) {
            System.out.print(a.name + ": ");
            x.addAstra(Astra.readFromString(keyboard.nextLine()));
        }
        return x;
    }


}
