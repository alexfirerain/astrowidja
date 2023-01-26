package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;
import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;

import java.util.*;

import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Settings.*;


/**
 * Основной сценарий, описывающий работу
 * программы как таковой
 */
public class Application {
    static final Scanner keyboard = new Scanner(System.in);
    static public int id = 0;

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

        if (autosave)
            Storage.saveTableToFile(DESK, autosaveName());
    }


    private static void printChartStat(ChartObject chart) {
        System.out.println(chart.getAstrasList());
        System.out.println(chart.getAspectTable());
        System.out.println(chart.resonanceAnalysisVerbose(Settings.getEdgeHarmonic()));
    }

    private static void welcome() {
        System.out.printf("%sСчитаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n%n",
                frameText("Начато исполнение АстроВидьи!", 30, '*'),
                getPrimalOrb(), getOrbDivisor(), getEdgeHarmonic());
    }

    /**
     * Стол, на котором лежат карты, загруженные в АстроВидью.
     */
    static private final Map<String, ChartObject> DESK = new HashMap<>();

    static private final List<ChartObject> TABLE = new ArrayList<>();

    static private final ChartList LIST = new ChartList();

    private static void mainCycle() {
        String MENU = frameText("""
                1. карты на столе
                2. настройки
                3. управление картами
                4. показать карту
                5. добавить карту с клавиатуры
                0. выход
                    """, 20, 60, DOUBLE_FRAME);
        boolean exit = false;
        while (!exit) {
            System.out.println(MENU);
            switch (keyboard.nextLine()) {
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
     * Выводит на экран список карт, лежащих на {@link #TABLE столе}, то есть загруженных в программу.
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
        System.out.println(frameText(output.toString(),
                30, 80,
                SINGULAR_FRAME));
    }

    /**
     * Запрашивает, какую карту со {@link #TABLE стола} взять в работу,
     * т.е. запустить в {@link #workCycle(ChartObject) цикле процедур для карты}.
     */
    public static void takeChart() {
        System.out.print("Укажите карту по имени или номеру на столе: ");
        String order = keyboard.nextLine();
        if (order.isBlank())
            return;
        if (order.matches("^\\d+")) {
            try {
                int i = Integer.parseInt(order);
                if (i > 0 && i <= TABLE.size())
                    workCycle(TABLE.get(i - 1));
                else
                    System.out.println("На столе всего " + TABLE.size() + " карт.");
            } catch (NumberFormatException e) {
                System.out.println("Число не распознано.");
            }
        } else {
            TABLE.stream()
                    .filter(co -> co.getName().equals(order))
                    .findFirst()
                    .ifPresentOrElse(Application::workCycle,
                            () -> System.out.println("Карты '" + order + "' нет на столе."));
        }
    }

    private static void workCycle(ChartObject chart) {

    }

    /**
     * Создаёт карту на основе юзерского ввода.
     * Предлагает ввести координаты в виде "градусы минуты секунды"
     * для каждой стандартной {@link AstraEntity АстроСущности}. Затем предлагает вводить
     * дополнительные {@link Astra астры} в виде "название градусы минуты секунды".
     * Пустой ввод означает пропуск астры или отказ от дополнительного ввода.
     *
     * @return {@link Chart одиночную карту}, созданную на основе ввода.
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
     * Добавляет карту на {@link #TABLE стол}. Если карта с таким именем уже
     * присутствует, запрашивает решение у юзера.
     * @param chart добавляемая карта.
     */
    private static void addChart(ChartObject chart) {
        if (ChartList.containsName(TABLE, chart.getName()) ?
                ChartList.mergeResolving(chart, TABLE, "на столе") :
                TABLE.add(chart))
            System.out.println("Карта загружена: " + chart);
    }

    /**
     * Добавляет {@link #TABLE в реестр} произвольное количество карт из аргументов.
     * Если какая-то карта совпадает с уже записанной, у юзера
     * запрашивается решение.
     * @param charts добавляемые карты.
     */
    private static void addChart(ChartObject... charts) {
        Arrays.stream(charts)
                .forEach(Application::addChart);
    }

    /**
     * Прочитывает карты из файла в папке базы данных на стол.
     *
     * @param file имя файла в папке базы данных.
     */
    public static void loadFromFile(String file) {
        LIST.mergeResolving(Storage.readChartsFromFile(file), "на столе");
    }

}
