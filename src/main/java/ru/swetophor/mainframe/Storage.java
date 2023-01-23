package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;
import ru.swetophor.celestialmechanics.ChartType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.mainframe.Application.keyboard;

/**
 * Предоставляет модель хранения данных в файлах попки базы данных.
 * Содержит список всех доступных карт и инструментарий по управлению картами.
 */
public class Storage {
    static final String baseDir = "base";

    static {
        Path basePath = Path.of(Storage.baseDir);
        if (!Files.exists(basePath)) {
            try {
                Files.createDirectory(basePath);
                System.out.printf("Создали папку '%s'%n", Storage.baseDir);
            } catch (IOException e) {
                System.out.printf("Не удалось создать папку %s: %s%n", Storage.baseDir, e.getLocalizedMessage());
            }
        }
    }

    static File base = new File(baseDir);
    static List<File> baseContent = getBaseContent();

    public static String displayFolder() {
        baseContent = getBaseContent();
        if (baseContent == null) return null;
        return IntStream.range(0, baseContent.size())
                .mapToObj(i -> (i + 1) + ". " + baseContent.get(i).getName() + "\n")
                .collect(Collectors.joining());
    }

    static void manageCharts() {
        System.out.println("В базе присутствуют следующие файлы и карты:");

        String content = reportFullBasesContent();
        if (content == null)
            content = "Не удалось получить содержимое базы.";

        System.out.println(Decorator.frameText(content, 40, '*'));
    }

    private static List<File> getBaseContent() {
        if (base.listFiles() == null)
            return null;
        return Arrays.stream(Objects.requireNonNull(base.listFiles()))
                .filter(x -> !x.isDirectory())
                .filter(file -> file.getName().endsWith(".awb") || file.getName().endsWith(".awc"))
                .collect(Collectors.toList());
    }


    private static String reportFullBasesContent() {
        if (baseContent == null)
            return null;
        List<String> fileNames = baseContent.stream().map(File::getName).toList();
        StringBuilder output = new StringBuilder();
        for (String filename : fileNames) {
            output.append(filename).append("\n");
            try {
                String[] names = getNames(filename);

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

    private static String[] getNames(String filename) throws IOException {
        return Files.readString(Path.of(baseDir, filename))
                .lines()
                .filter(line -> line.startsWith("#") && line.length() > 1)
                .map(line -> line.substring(1))
                .toArray(String[]::new);
    }

    private static String[] getNames(File filename) throws IOException {
        return Files.readString(filename.toPath())
                .lines()
                .filter(line -> line.startsWith("#") && line.length() > 1)
                .map(line -> line.substring(1))
                .toArray(String[]::new);
    }

    public static void saveTableToFile(Map<String, ChartObject> table, String target) {
        StringBuilder content = new StringBuilder();
        table.values().stream()
                .filter(chart -> chart.getType() == ChartType.COSMOGRAM)
                .forEach(chart -> content.append(chart.getString()));
        try (PrintWriter out = new PrintWriter(Path.of(baseDir, target).toFile())) {
            // TODO: if exists
            out.println(content);
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", target, e);
        }
        System.out.printf("Строка%n%s%n записана в %s%n", content, target);
    }

    public static void moveChartsToFile(String source, String target, int... charts) {

    }

    public static void putChartToBase(ChartObject chart, String file) {
        List<ChartObject> content = readChartsFromFile(file);
        if (containsName(content, chart.getName())) {
            boolean fixed = false;
            while (!fixed) {
                System.out.printf("""
                                                
                        Карта с именем %s уже есть в файле %s:
                        1. заменить
                        2. добавить под новым именем
                        0. отмена
                        """, chart.getName(), file);
                switch (keyboard.nextLine()) {
                    case "1" -> {
                        content.stream()
                                .filter(c -> c.getName().equals(chart.getName()))
                                .findFirst()
                                .ifPresent(content::remove);
                        fixed = true;
                    }
                    case "2" -> {
                        System.out.print("Новое имя: ");
                        String name = keyboard.nextLine();
                        System.out.println();
                        while (containsName(content, name)) {
                            System.out.print("Новое имя: ");
                            name = keyboard.nextLine();
                            System.out.println();
                        }
                        chart.setName(name);
                        fixed = true;
                    }
                    case "0" -> {
                        System.out.println("Отмена загрузки карты: " + chart.getName());
                        return;
                    }
                }
            }
        }
        content.add(chart);
        dropListToFile(content, file);
    }

    private static void dropListToFile(List<? extends ChartObject> content, String file) {
        try (PrintWriter out = new PrintWriter(Path.of(baseDir, file).toFile())) {
            // TODO: if exists
            out.println(content.stream()
                    .map(ChartObject::getString)
                    .collect(Collectors.joining()));
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", file, e);
        }
        System.out.printf("Карты {%s} записаны в файл %s.%n",
                content.stream()
                        .map(ChartObject::getName)
                        .collect(Collectors.joining(", ")),
                file);
    }

    /**
     * Содержит ли указанный список указанное имя карты.
     *
     * @param content   список, в котором проверяем.
     * @param chartName проверяемое имя.
     * @return есть ли карта с таким именем в этом списке.
     */
    private static boolean containsName(List<? extends ChartObject> content, String chartName) {
        return content.stream()
                .anyMatch(c -> c.getName()
                        .equals(chartName));
    }

    /**
     * Прочитывает список карт из формата *.awb.
     * Если файл не существует, то пустой список.
     *
     * @param file указанный файл.
     * @return список карт, прочитанных из файла.
     */
    public static List<Chart> readChartsFromFile(File file) {
        if (!file.exists()) {
            System.out.printf("Не удалось найти файл '%s'%n", file);
            return new ArrayList<>();
        }
        try {
            return Arrays.stream(Files.readString(file.toPath())
                            .split("#"))
                    .filter(s -> !s.isBlank())
                    .map(Chart::readFromString)
                    .toList();
        } catch (IOException e) {
            System.out.printf("Не удалось прочесть файл '%s': %s%n", file, e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Проверяет, есть ли в указанном файле карта с указанным именем.
     *
     * @param baseFile указанный файл.
     * @param chart    указанная карта.
     * @return присутствует ли запись с указанным именем в указанном файле *.awb.
     */
    public static boolean containsChart(File baseFile, ChartObject chart) {
        try {
            return Arrays.stream(
                            getNames(baseFile))
                    .anyMatch(n -> n.equals(chart.getName()));
        } catch (IOException e) {
            System.out.printf("Не удалось прочесть %s: %s", baseFile.getName(), e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Прочитывает список карт из формата *.awb
     *
     * @param file имя файла
     * @return список карт, прочитанных из файла.
     * Если файл не существует, то пустой список.
     */
    public static List<ChartObject> readChartsFromFile(String file) {
        List<ChartObject> result = new ArrayList<>();
        Path filePath = Path.of(baseDir, file);
        if (!Files.exists(filePath)) {
            System.out.printf("Не удалось найти файл '%s'%n", file);
        } else {
            try {
                result = Arrays.stream(Files.readString(filePath)
                                .split("#"))
                        .filter(s -> !s.isBlank())
                        .map(ChartObject::readFromString)
                        .toList();
            } catch (IOException e) {
                System.out.printf("Не удалось прочесть файл '%s': %s%n", file, e.getLocalizedMessage());
            }
        }
        return result;
    }

    /**
     * Разрешает коллизию, возникающую, если имя добавляемой карты уже содержится
     * в списке. Запрашивает решение у астролога, требуя выбора одного из трёх вариантов:
     * <li>заменить – удаляет из списка карту с конфликтным именем, возвращает добавляемую;</li>
     * <li>переименовать – запрашивает новое имя для добавляемой карты, обновляет её и возвращает;</li>
     * <li>отмена – возвращает {@code null}</li>
     * <p>
     * Таким образом, возращаемое функцией значение соответствует той карте, которую
     * следует слудующим шагом добавить в целевой список.
     *
     * @param controversial добавляемая карта, имя которой, как предварительно уже определено,
     *                      уже присутствует в целевом списке.
     * @param list          список, куда должны добавляться карты с уникальными именами.
     * @param listName      название файла или иного списка, в который добавляется карта, в предложном падеже.
     * @return ту же карту, если выбрано "заменить старую карту",
     * ту же карту с новым именем, если выбрано "переименовать новую карту",
     * или {@code пусто}, если выбрано "отменить операцию".
     */
    public ChartObject resolveCollision(ChartObject controversial, List<ChartObject> list, String listName) {
        boolean fixed = false;
        while (!fixed) {
            System.out.printf("""
                                            
                    Карта с именем %s уже есть:
                    1. заменить присутствующую в %s
                    2. добавить под новым именем
                    0. отмена
                    """, controversial.getName(), listName);
            switch (keyboard.nextLine()) {
                case "1" -> {
                    for (ChartObject c : list)
                        if (c.getName().equals(controversial.getName())) {
                            list.remove(c);
                            break;
                        }
                    fixed = true;
                }
                case "2" -> {
                    String name;
                    do {
                        System.out.print("Новое имя: ");
                        name = keyboard.nextLine();
                        System.out.println();
                    } while (containsName(list, name));
                    controversial.setName(name);
                    fixed = true;
                }
                case "0" -> {
                    System.out.println("Отмена добавления карты: " + controversial.getName());
                    return null;
                }
            }
        }
        return controversial;
    }

    public List<ChartObject> mergeList(List<ChartObject> addingCharts, List<ChartObject> mergingList, String listName) {
        if (addingCharts == null || addingCharts.isEmpty())
            return mergingList;
        if (mergingList == null || mergingList.isEmpty())
            return addingCharts;
        for (ChartObject adding : addingCharts) {
            ChartObject inspected = adding;
            if (containsName(mergingList, adding.getName()))
                inspected = resolveCollision(adding, mergingList, listName);
            if (inspected != null)
                mergingList.add(inspected);
        }
        return mergingList;
    }
}
