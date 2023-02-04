package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;
import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;

import java.util.Scanner;

import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Settings.*;


/**
 * Основной сценарий, описывающий работу
 * программы как таковой
 */
public class Application {
    static final Scanner KEYBOARD = new Scanner(System.in);
    static public int id = 0;

    static String SW = """
                Сева
                Солнце 81 5 8
                Луна 348 59 25
                Венера 35 19 47
                Марс 143 35 4
                """;

    protected static final ChartList DESK = new ChartList();


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
            switch (KEYBOARD.nextLine()) {
                case "1" -> listCharts();
                case "2" -> Settings.editSettings();
                case "3" -> Storage.fullBaseReport();
                case "4" -> takeChart();
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
        System.out.print("Укажите карту по номеру на столе или по имени: ");
        String order = KEYBOARD.nextLine();
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
     * Выводит на экран список карт, лежащих на {@link #DESK столе}, то есть загруженных в программу.
     */
    private static void listCharts() {
        System.out.println(frameText(DESK.isEmpty() ?
                        "Ни одной карты не загружено." :
                        DESK.toString(),
                30, 80, SINGULAR_FRAME));
    }

    /**
     * Запрашивает, какую карту со {@link #DESK стола} взять в работу,
     * т.е. запустить в {@link #workCycle(ChartObject) цикле процедур для карты}.
     */
    public static void takeChart() {    // TODO: абстрагировать процедуру поиска на столе по номеру или имени
        System.out.print("Укажите карту по имени или номеру на столе: ");
        String order = KEYBOARD.nextLine();
        ChartObject taken = DESK.findChart(order, "на столе");
        if (taken == null) return;
        workCycle(taken);
    }

    private static void workCycle(ChartObject chart) {
        String CHART_MENU = """
                    действия с картой:
                "-> имя_файла"   = сохранить в файл
                "+карта" = построить синастрию
                "*карта" = построить композит
                 
                "1" = о положениях астр
                "2" = о резонансах
                "3" = о паттернах кратко
                "4" = о паттернах со статистикой
                """;
        System.out.println(chart.getCaption());
        System.out.println(chart.getAstrasList());
        System.out.println(Decorator.frameText(CHART_MENU, 50, 100, SINGULAR_FRAME));
        String input;
        while (true) {
            input = KEYBOARD.nextLine();
            if (input == null || input.isBlank()) return;

            if (input.startsWith("->")) {
                Storage.putChartToBase(chart, input.substring(2).trim());
            } else if (input.startsWith("+")) {

            } else if (input.startsWith("*")) {

            }
            else switch (input) {
                case "1" -> System.out.println(chart.getAstrasList());
                case "2" -> System.out.println(chart.getAspectTable());
                case "3" -> System.out.println(chart.resonanceAnalysis(getEdgeHarmonic()));
                case "4" -> System.out.println(chart.resonanceAnalysisVerbose(getEdgeHarmonic()));
                default -> System.out.println(frameText(CHART_MENU, 50, 100, SINGULAR_FRAME));
            }
        }
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
        Chart x = new Chart(KEYBOARD.nextLine());
        for (AstraEntity a : AstraEntity.values()) {
            System.out.print(a.name + ": ");
            String input = KEYBOARD.nextLine();
            if (input.isBlank())
                continue;
            x.addAstra(Astra.readFromString(a.name + " " + input));
            System.out.println();
        }
        System.out.println("Ввод дополнительных астр в формате 'название градусы минуты секунды'");
        String input = KEYBOARD.nextLine();
        while (!input.isBlank()) {
            x.addAstra(Astra.readFromString(input));
            input = KEYBOARD.nextLine();
        }
        return x;
    }

    /**
     * Добавляет карту на {@link #DESK стол}. Если карта с таким именем уже
     * присутствует, запрашивает решение у юзера.
     *
     * @param chart добавляемая карта.
     */
    private static void addChart(ChartObject chart) {
        if (DESK.addResolving(chart, "на столе"))
            System.out.println("Карта загружена на стол: " + chart);
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

    /**
     * Прочитывает карты из файла в папке базы данных на {@link Application#DESK стол} {@link Application АстроВидьи}.
     *
     * @param fileName имя файла в папке базы данных.
     */
    public static void loadFromFile(String fileName) {
        Storage.readChartsFromFile(fileName)
                .forEach(c -> DESK.addResolving(c, "на столе"));
    }
}
