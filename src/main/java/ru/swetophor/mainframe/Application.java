package ru.swetophor.mainframe;


import ru.swetophor.celestialmechanics.*;

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

    /**
     * Статический список карт в памяти работающей АстроВидьи.
     */
    protected static final ChartList DESK = new ChartList();


    private static void printChartStat(ChartObject chart) {
        print(chart.getAstrasList());
        print(chart.getAspectTable());
        print(chart.resonanceAnalysisVerbose(Settings.getEdgeHarmonic()));
    }

    private static void welcome() {
        System.out.printf("%sСчитаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n%n",
                asteriskFrame("Начато исполнение АстроВидьи!"),
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
            Storage.saveTableToFile(DESK, Storage.autosaveName());
    }

    /**
     * Основное меню и основной рабочий цикл АстроВидьи.
     */
    private static void mainCycle() {
        listCharts();
        String MENU = """
                1. карты на столе
                2. настройки
                3. списки карт
                4. работа с картой
                5. добавить карту с клавиатуры
                0. выход
                """;
        boolean exit = false;
        while (!exit) {
            printInDoubleFrame(MENU);
            switch (KEYBOARD.nextLine()) {
                case "1" -> listCharts();
                case "2" -> Settings.editSettings();
                case "3" -> Storage.fullBaseReport();
                case "4" -> takeChart();
                case "5" -> addChart(enterChartData());
                case "0" -> exit = true;
            }
        }
        print("Спасибо за ведание резонансов!");
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
    protected static void listCharts() {
        printInFrame(DESK.isEmpty() ?
                "Ни одной карты не загружено." :
                DESK.toString()
        );
    }

    /**
     * Запрашивает, какую карту со {@link #DESK стола} взять в работу,
     * т.е. запустить в {@link #workCycle(ChartObject) цикле процедур для карты}.
     * Если карта не опознана по номеру на столе или имени, не делает ничего.
     * Но функция поиска сама выводит сообщения, если не нашла.
     */
    public static void takeChart() {
        System.out.print("Укажите карту по имени или номеру на столе: ");
        String order = KEYBOARD.nextLine();
        ChartObject taken = DESK.findChart(order, "на столе");
        if (taken == null) return;
        workCycle(taken);
    }

    /**
     * Цикл работы с картой.
     * Предоставляет действия, которые можно выполнить с картой: просмотр статистики,
     * сохранение в список (файл), построение средней и синастрической карт.
     *
     * @param chart карта, являющаяся предметом работы.
     */
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
        print(chart.getCaption());
        print(chart.getAstrasList());
        printInFrame(CHART_MENU);
        String input;
        while (true) {
            input = KEYBOARD.nextLine();
            if (input == null || input.isBlank()) return;

            if (input.startsWith("->")) {
                Storage.putChartToBase(chart, input.substring(2).trim());
            } else if (input.startsWith("+") && chart instanceof Chart) {
                ChartObject counterpart = DESK.findChart(input.substring(1).trim(), "на столе");
                if (counterpart instanceof Chart)
                    addChart(new Synastry((Chart) chart, (Chart) counterpart));
            } else if (input.startsWith("*") && chart instanceof Chart) {
                ChartObject counterpart = DESK.findChart(input.substring(1).trim(), "на столе");
                if (counterpart instanceof Chart)
                    addChart(Chart.composite((Chart) chart, (Chart) counterpart));
            }
            else switch (input) {
                    case "1" -> print(chart.getAstrasList());
                    case "2" -> print(chart.getAspectTable());
                    case "3" -> print(chart.resonanceAnalysis(getEdgeHarmonic()));
                    case "4" -> print(chart.resonanceAnalysisVerbose(getEdgeHarmonic()));
                    default -> printInFrame(CHART_MENU);
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
            print();
        }
        print("Ввод дополнительных астр в формате 'название градусы минуты секунды'");
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

    /**
     * Прочитывает карты из файла в папке базы данных на {@link #DESK стол} {@link Application АстроВидьи}.
     *
     * @param fileName имя файла в папке базы данных.
     */
    public static void loadFromFile(String fileName) {
        Storage.readChartsFromFile(fileName)
                .forEach(c -> DESK.addResolving(c, "на столе"));
        print("Загружены карты из " + fileName);
    }


}
