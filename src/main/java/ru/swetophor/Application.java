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


        mainMenu();
    }


    private static void printChartStat(ChartObject chart) {
        if (chart instanceof Chart) {
            System.out.println(((Chart) chart).getAstrasList());
            System.out.println(((Chart) chart).getAspectTable());
        }
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
        System.out.println(MENU);
    }

    static private final Map<String, ChartObject> table = new HashMap<>();

    private static final String MENU = """
            1. загруженные карты
            2. настройки
            3. управление картами
            4. показать карту
            5. добавить карту с клавиатуры
            0. выход
            """;

    private static void mainMenu() {
        boolean exit = false;
        while(!exit) {
            switch(keyboard.nextLine()) {
                case "1" -> listCharts();
                case "4" -> showChart();
                case "5" -> addChart(enterChartData());
                case "0" -> exit = true;
            }
        }
    }

    private static void showChart() {
        System.out.print("Укажите карту по ИД или имени: ");
        String order = keyboard.nextLine();
        if (order.isBlank()) {
            return;
        }
        if (order.matches("//d*")) {
            try {
                int i = Integer.parseInt(order);
                table.values().stream()
                        .filter(chart -> chart.getID() == i)
                        .findFirst().ifPresentOrElse(
                                Application::printChartStat,
                                () -> System.out.println("Карты с таким номером не найдено."));
            } catch (NumberFormatException e) {
                System.out.println("Число не распознано.");
            }
        } else {
            if (table.containsKey(order))
                printChartStat(table.get(order));
            else
                System.out.println("Карты с таким именем не найдено.");
        }
        System.out.println(MENU);
    }

    private static void listCharts() {
        table.values().forEach(System.out::println);
    }

    private static Chart enterChartData() {
        System.out.print("Название новой карты: ");
        Chart x = new Chart(keyboard.nextLine());
        for (AstraEntity a : AstraEntity.values()) {
            System.out.print(a.name + ": ");
            x.addAstra(Astra.readFromString(keyboard.nextLine()));
            System.out.println();
        }
        return x;
    }

    private static void addChart(ChartObject chart) {
        if (table.containsKey(chart.getName())) {
            boolean fixed = false;
            while (!fixed) {
                System.out.println("Карта с таким именем уже построена:\n" +
                        "1. заменить\n" +
                        "2. сохранить под новым именем\n" +
                        "0. отмена");
                switch (keyboard.nextLine()) {
                    case "2" -> {
                        System.out.print("Новое имя: ");
                        String name = keyboard.nextLine();
                        System.out.println();
                        while (table.containsKey(name)) {
                            System.out.print("Новое имя: ");
                            name = keyboard.nextLine();
                            System.out.println();
                        }
                        chart.setName(name);
                        fixed = true;
                    }
                    case "1" -> fixed = true;
                    case "0" -> {
                        System.out.println("Отмена загрузки карты: " + chart);
                        return;
                    }
                }
            }
        }
        table.put(chart.getName(), chart);
        System.out.println("Карта загружена: " + chart);
    }


}
