package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;
import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ru.swetophor.mainframe.Settings.*;


/**
 * Основной сценарий, описывающий работу
 * программы как таковой
 */
public class Application {
    static final Scanner keyboard = new Scanner(System.in);

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

//        Chart SCChart = Chart.readFromString(SC);
//        printChartStat(SCChart);

//        Chart TVChart = Chart.readFromString(TV);
//        printChartStat(TVChart);

//        Synastry doubleChart = new Synastry(SCChart, TVChart);
//        doubleChart.plotAspectTable();

//        Chart SCTVComposite = Chart.composite(SCChart, TVChart);
//        printChartStat(SCTVComposite);

//        addChart(SCChart, TVChart, doubleChart, SCTVComposite);

        loadFromFile("сохранение вс 15 января .23 15-03.awb");
//        loadFromFile("autosave.awb");

        mainCycle();

        Storage.saveTableToFile(DESK, Decorator.autosaveName());
    }


    private static void printChartStat(ChartObject chart) {
        System.out.println(chart.getAstrasList());
        System.out.println(chart.getAspectTable());
        System.out.println(chart.resonanceAnalysis(Settings.getEdgeHarmonic()));
    }

    static public int id = 0;

    private static void welcome() {
        System.out.printf("%sСчитаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n%n",
                Decorator.frameText("Начато исполнение АстроВидьи!", 30, '*'),
                getPrimalOrb(), getOrbDivisor(), getEdgeHarmonic());
    }

    /**
     * Стол, на котором лежат карты, загруженные в АстроВидью.
     */
    static private final Map<String, ChartObject> DESK = new HashMap<>();

    private static void displayMainMenu() {
        System.out.println(Decorator.frameText("""
            1. карты на столе
            2. настройки
            3. управление картами
            4. показать карту
            5. добавить карту с клавиатуры
            0. выход
                """, 20, 60,
                '╔', '═', '╗',
                '║', '╚', '╝'));
    }

    private static void mainCycle() {
        boolean exit = false;
        while(!exit) {
            displayMainMenu();
            switch(keyboard.nextLine()) {
                case "1" -> listCharts();
                case "2" -> Settings.editSettings();
                case "3" -> Storage.manageCharts();
                case "4" -> showChart();
                case "5" -> addChart(enterChartData());
                case "0" -> exit = true;
            }
        }
        System.out.println("Спасибо за ведание резонансов!");
    }

    /**
     * Запрашивает карту по имени или номеру на столе,
     * если она найдена, выводит её статистику на экран.
     */
    private static void showChart() {
        System.out.print("Укажите карту по ИД или имени: ");
        String order = keyboard.nextLine();
        if (order.isBlank())
            return;
        if (order.matches("^\\d+"))
            try {
                int i = Integer.parseInt(order);
                DESK.values().stream()
                        .filter(chart -> chart.getID() == i)
                        .findFirst().ifPresentOrElse(
                                Application::printChartStat,
                                () -> System.out.println("Карты с номером " + order + " не найдено."));
            } catch (NumberFormatException e) {
                System.out.println("Число не распознано.");
            }
        else if (DESK.containsKey(order))
            printChartStat(DESK.get(order));
        else
            System.out.println("Карты с именем " + order + " не найдено.");


    }

    /**
     * Выводит на экран список карт, лежащих на столе, то есть загруженных в программу.
     */
    private static void listCharts() {
        StringBuilder output = new StringBuilder();
        if (DESK.isEmpty())
            output
                    .append("Ни одной карты не загружено.");
        else
            DESK.values()
                    .forEach(c -> output
                            .append(c.toString())
                            .append("\n"));
        System.out.println(Decorator.frameText(output.toString(),
                30, 80,
                '┌', '─', '┐',
                '│', '└', '┘'));
    }

    /**
     * Создаёт карту на основе юзерского ввода.
     * Предлагает ввести координаты в виде "градусы минуты секунды"
     * для каждой стандартной АстроСущности. Затем предлагает вводить
     * дополнительные астры в виде "название градусы минуты секунды".
     * Пустой ввод означает пропуск астры или отказ от дополнительного ввода.
     * @return  одиночную карту, созданную на основе ввода.
     */
    private static Chart enterChartData() {
        System.out.print("Название новой карты: ");
        Chart x = new Chart(keyboard.nextLine());
        for (AstraEntity a : AstraEntity.values()) {
            System.out.print(a.name + ": ");
            String input = keyboard.nextLine();
            if (input.isBlank())
                continue;
            x.addAstra(Astra.readFromString(a.name + " " + input));
            System.out.println();
        }
        System.out.println("Ввод дополнительных астр в формате 'название градусы минуты секунды'");
        String input = keyboard.nextLine();
        while (!input.isBlank()) {
            x.addAstra(Astra.readFromString(input));
            input = keyboard.nextLine();
        }
        return x;
    }

    /**
     * Добавляет карту в реестр карт. Если карта с таким именем уже
     * присутствует, запрашивает решение у юзера.
     * @param chart добавляемая карта.
     */
    private static void addChart(ChartObject chart) {
        if (DESK.containsKey(chart.getName())) {
            boolean fixed = false;
            while (!fixed) {
                System.out.printf("""
                                                
                        Карта с именем %s уже загружена:
                        1. заменить
                        2. добавить под новым именем
                        0. отмена
                        """, chart.getName());
                switch (keyboard.nextLine()) {
                    case "1" -> fixed = true;
                    case "2" -> {
                        System.out.print("Новое имя: ");
                        String name = keyboard.nextLine();
                        System.out.println();
                        while (DESK.containsKey(name)) {
                            System.out.print("Новое имя: ");
                            name = keyboard.nextLine();
                            System.out.println();
                        }
                        chart.setName(name);
                        fixed = true;
                    }
                    case "0" -> {
                        System.out.println("Отмена загрузки карты: " + chart);
                        return;
                    }
                }
            }
        }
        DESK.put(chart.getName(), chart);
        System.out.println("Карта загружена: " + chart);
    }

    /**
     * Добавляет в реестр произвольное количество карт из аргументов.
     * Если какая-то карта совпадает с уже записанной, у юзера
     * запрашивается решение.
     * @param charts добавляемые карты.
     */
    private static void addChart(ChartObject... charts) {
        Arrays.stream(charts)
                .forEach(Application::addChart);
    }

    /**
     * Прочитывает карты из файла в папке базы данных.
     *
     * @param file имя файла в папке базы данных.
     */
    public static void loadFromFile(String file) {
        Path source = Path.of(Storage.baseDir, file);
        if (!Files.exists(source)) {
            System.out.printf("Не удалось найти файл '%s'%n", file);
            return;
        }
        try {
            Arrays.stream(Files.readString(source)
                    .split("#"))
                    .filter(s -> !s.isBlank())
                    .map(Chart::readFromString)
                    .forEach(Application::addChart);
        } catch (IOException e) {
            System.out.printf("Не удалось прочесть файл '%s'%n", file);
        }

    }

}
