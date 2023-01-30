package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Chart;
import ru.swetophor.celestialmechanics.ChartObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    protected static File base = new File(baseDir);
    protected static List<File> baseContent = getBaseContent();

    protected static List<ChartList> chartLibrary = scanLibrary();

    public static List<ChartList> scanLibrary() {
        List<File> list = getBaseContent();
        return list != null ?
                list.stream()
                        .map(f -> readChartsFromFile(f.getName()))
                        .toList() :
                new ArrayList<>();
    }


    public static String showLibrary() {
        baseContent = getBaseContent();
        if (baseContent == null) return null;
        return IntStream.range(0, baseContent.size())
                .mapToObj(i -> (i + 1) + ". " + baseContent.get(i).getName() + "\n")
                .collect(Collectors.joining());
    }

    static void manageCharts() {
        System.out.println("В базе присутствуют следующие файлы и карты:");

        String content = reportBaseContentExpanded();
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


    private static String reportBaseContentExpanded() {
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

    public static void saveTableToFile(ChartList table, String target) {
        ChartList fileContent = readChartsFromFile(target);
        fileContent.addAll(table);
        String drop = fileContent.getString();

        try (PrintWriter out = new PrintWriter(Path.of(baseDir, target).toFile())) {
            out.println(drop);
            System.out.printf("Строка%n%s%n записана в %s%n", drop, target);
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", target, e.getLocalizedMessage());
        }
    }

    public static void moveChartsToFile(String source, String target, int... charts) {

    }

    public static void putChartToBase(ChartObject chart, String file) {
        ChartList fileContent = readChartsFromFile(file);
        if (fileContent.add(chart))
            dropListToFile(fileContent, file);
    }

    /**
     * Прочитывает список карт из формата *.awb
     *
     * @param file имя файла в папке данных.
     * @return список карт, прочитанных из файла.
     * Если файл не существует, то пустой список.
     */
    public static ChartList readChartsFromFile(String file) {
        ChartList read = new ChartList();
        Path filePath = Path.of(baseDir, file);
        if (!Files.exists(filePath)) {
            System.out.printf("Не удалось обнаружить файла '%s'%n", file);
        } else {
            try {
                Arrays.stream(Files.readString(filePath)
                                .split("#"))
                        .filter(s -> !s.isBlank())
                        .map(ChartObject::readFromString)
                        .forEach(read::add);
            } catch (IOException e) {
                System.out.printf("Не удалось прочесть файл '%s': %s%n", file, e.getLocalizedMessage());
            }
        }
        return read;
    }

    /**
     * Записывает содержимое картосписка (как возвращается {@link ChartList#getString()}
     * в файл по указанному адресу (относительно рабочей папки).
     * Существующий файл заменяется, несуществующий создаётся.
     *
     * @param content список карт, чьё содержимое записывается.
     * @param file    имя файла в рабочей папке.
     */
    private static void dropListToFile(ChartList content, String file) {
        try (PrintWriter out = new PrintWriter(Path.of(baseDir, file).toFile())) {
            out.println(content.getString());
            System.out.printf("Карты {%s} записаны в файл %s.%n",
                    String.join(", ", content.getNames()),
                    file);
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", file, e.getLocalizedMessage());
        }
    }

    /**
     * Проверяет, есть ли в указанном файле карта с указанным именем.
     *
     * @param baseFile указанный файл.
     * @param chart    указанная карта.
     * @return присутствует ли запись с указанным именем в указанном файле *.awb.
     */
    public static boolean containsChartName(File baseFile, ChartObject chart) {
        try {
            return Arrays.stream(
                            getNames(baseFile))
                    .anyMatch(n -> n.equals(chart.getName()));
        } catch (IOException e) {
            System.out.printf("Не удалось прочесть %s: %s", baseFile.getName(), e.getLocalizedMessage());
            return false;
        }
    }


}
