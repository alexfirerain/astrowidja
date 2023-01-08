package ru.swetophor;


import ru.swetophor.celestialmechanics.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.Settings.*;


/**
 * Основной сценарий, описывающий работу
 * программы как таковой
 */
public class Application {
    private static final Scanner keyboard = new Scanner(System.in);

    private static final String baseDir = "base";
    static {
        Path basePath = Path.of(baseDir);
        if (!Files.exists(basePath)) {
            try {
                Files.createDirectory(basePath);
                System.out.printf("Создали папку '%s'%n", baseDir);
            } catch (IOException e) {
                System.out.printf("Не удалось создать папку %s: %s%n", baseDir, e.getLocalizedMessage());
            }
        }
    }

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
//        printChartStat(SCChart);

        Chart TVChart = Chart.readFromString(TV);
//        printChartStat(TVChart);

        Synastry doubleChart = new Synastry(SCChart, TVChart);
//        doubleChart.plotAspectTable();

        Chart SCTVComposite = Chart.composite(SCChart, TVChart);
//        printChartStat(SCTVComposite);

        addChart(SCChart, TVChart, doubleChart, SCTVComposite);

        loadFromFile("autosave.awb");

        mainCycle();

        saveTableToFile(Shell.autosaveName());
    }


    private static void printChartStat(ChartObject chart) {
            System.out.println(chart.getAstrasList());
            System.out.println(chart.getAspectTable());
    }

    static public int id = 0;

    private static void welcome() {
        System.out.printf("%sСчитаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n%n",
                Shell.frameText("Начато исполнение АстроВидьи!", 30, '*'),
                getOrbs(), orbsDivider, edgeHarmonic);
    }

    /**
     * Стол, на котором лежат карты, загруженные в АстроВидью.
     */
    static private final Map<String, ChartObject> DESK = new HashMap<>();

    private static void displayMainMenu() {
        System.out.println(Shell.frameText("""
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
                case "3" -> manageCharts();
                case "4" -> showChart();
                case "5" -> addChart(enterChartData());
                case "0" -> exit = true;
            }
        }
        System.out.println("Спасибо за ведание резонансов!");
    }

    private static void manageCharts() {
        System.out.println("В базе присутствуют следующие файлы и карты:");

        String content = reportBasesContent();
        if (content == null)
            content = "Не удалось получить содержимое базы.";

        System.out.println(Shell.frameText(content, 40, '*'));

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
        if (DESK.isEmpty())
            System.out.println("Ни одной карты не загружено.");
        else
            DESK.values().forEach(System.out::println);
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
                        
                        Карта с именем %s уже записана:
                        1. заменить
                        2. сохранить под новым именем
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
     * Прочитывает карты из файла в попке базы данных.
     * @param file имя файла в папке базы данных.
     */
    public static void loadFromFile(String file) {
        Path source = Path.of(baseDir, file);
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

    public static void saveTableToFile(String target) {
        StringBuilder content = new StringBuilder();
        DESK.values().stream()
            .filter(chart -> chart.getType() == ChartType.COSMOGRAM)
            .forEach(chart -> {
                content.append("#%s%n"
                               .formatted(chart.getName()));
                chart.getAstras().stream()
                        .map(Astra::getString)
                        .forEach(content::append);
                content.append("\n");
            });
        try (PrintWriter out = new PrintWriter(Path.of(baseDir, target).toFile())) {
            // TODO: if exists
            out.println(content);
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", target, e);
        }
        System.out.printf("Строка%n%s%n записана в %s%n", content, target);
    }

    private static List<String> getBaseContent() {
        File base = new File(baseDir);

        if (base.listFiles() == null)
            return null;
        return Arrays.stream(Objects.requireNonNull(base.listFiles()))
                    .filter(x -> !x.isDirectory())
                    .map(File::getName)
                    .filter(name -> name.endsWith(".awb") || name.endsWith(".awc"))
                    .collect(Collectors.toList());
    }

    private static String reportBasesContent() {
        List<String> files = getBaseContent();
        if (files == null)
            return null;
        StringBuilder output = new StringBuilder();
        for (String filename : files) {
            output.append(filename).append("\n");
            try {
                String[] names = Files.readString(Path.of(baseDir, filename))
                        .lines()
                        .filter(line -> line.startsWith("#") && line.length() > 1)
                        .map(line -> line.substring(1))
                        .toArray(String[]::new);

                if (filename.endsWith(".awb"))
                    IntStream.range(0, names.length)
                        .mapToObj(i -> " %3d. %s%n"
                                .formatted(i + 1, names[i]))
                        .forEach(output::append);

                if (filename.endsWith(".awc") && names.length > 0)
                    output.append(names[0]);

            } catch (IOException e) {
                output.append("Файл %s не читается: %s"
                        .formatted(filename, e.getLocalizedMessage()));
            }
        }

        return output.toString();
    }

    private static void moveChartsToFile(String source, String target, int... charts) {

    }

}
