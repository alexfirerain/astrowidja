package ru.swetophor.mainframe;

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

    /**
     * Рабочая папка.
     */
    protected static File base = new File(baseDir);
    /**
     * Список файлов в рабочей папке.
     */
    protected static List<File> baseContent = getBaseContent();
    /**
     * Список списков карт, соответствующих файлам в рабочей папке.
     */
    protected static List<ChartList> chartLibrary = scanLibrary();

    /**
     * Прочитывает список файлов из рабочей папки.
     *
     * @return список имён файлов, присутствующих в рабочей папке в момент вызова.
     */
    protected static List<String> tableOfContents() {
        return getBaseContent().stream()
                .map(File::getName)
                .toList();
    }

    /**
     * Прочитывает содержание всех файлов с картами.
     * Если по какой-то причине таковых не найдено, то пустой список.
     *
     * @return список списков карт, соответствующих файлам в рабочей папке.
     */
    private static List<ChartList> scanLibrary() {
        return tableOfContents().stream()
                .map(Storage::readChartsFromFile)
                .toList();
    }


    public static String showLibrary() {
        baseContent = getBaseContent();
        if (baseContent == null) return null;
        return IntStream.range(0, baseContent.size())
                .mapToObj(i -> "%d. %s%n".formatted(i + 1, baseContent.get(i).getName()))
                .collect(Collectors.joining());
    }

    static void manageCharts() {
        System.out.println("В базе присутствуют следующие файлы и карты:");

        String content = reportBaseContentExpanded();
        if (content == null)
            content = "Не удалось получить содержимое базы.";

        System.out.println(Decorator.frameText(content, 40, '*'));
    }

    /**
     * Прочитывает и отдаёт список файлов в рабочей папке.
     *
     * @return список файлов в папке базы. Если путь к базе не определён,
     * или её файл не существует или не является папкой, то пустой список.
     */
    private static List<File> getBaseContent() {
        return base == null || !base.exists() || base.listFiles() == null ?
                new ArrayList<>() :
                Arrays.stream(Objects.requireNonNull(base.listFiles()))
                        .filter(file -> !file.isDirectory())
                        .filter(file -> file.getName().endsWith(".awb") || file.getName().endsWith(".awc"))
                        .collect(Collectors.toList());
    }


    private static String reportBaseContentExpanded() {
        chartLibrary = scanLibrary();

        StringBuilder output = new StringBuilder();
        List<String> tableOfContents = tableOfContents();

        for (int j = 0; j < tableOfContents.size(); j++) {
            String filename = tableOfContents.get(j);
            output.append(filename).append("\n");
            String[] names = chartLibrary.get(j).getNames().toArray(String[]::new);

            if (filename.endsWith(".awb"))
                IntStream.range(0, names.length)
                        .mapToObj(i -> " %3d. %s%n"
                                .formatted(i + 1, names[i]))
                        .forEach(output::append);

            if (filename.endsWith(".awc") && names.length > 0)
                output.append(names[0]);

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
        chartLibrary = scanLibrary();
        ChartList sourceList = chartLibrary.get(tableOfContents().indexOf(source));
        ChartList targetList = chartLibrary.get(tableOfContents().indexOf(target));

        Arrays.stream(charts)
                .mapToObj(sourceList::get)
                .toList()
                .stream()
                .filter(c -> targetList.addResolving(c, target))
                .map(ChartObject::getName)
                .forEach(sourceList::remove);

        if (!sourceList.equals(readChartsFromFile(source)))
            dropListToFile(sourceList, source);
        if (!targetList.equals(readChartsFromFile(target)))
            dropListToFile(targetList, target);
    }

    public static void putChartToBase(ChartObject chart, String file) {
        ChartList fileContent = readChartsFromFile(file);
        if (fileContent.add(chart))
            dropListToFile(fileContent, file);
    }

    /**
     * Прочитывает список карт из формата *.awb
     *  Если файл не существует или чтение обламывается,
     *  выводит об этом сообщение
     * @param file имя файла в папке данных.
     * @return список карт, прочитанных из файла.
     * Если файл не существует или не читается, то пустой список.
     */
    public static ChartList readChartsFromFile(String file) {
        ChartList read = new ChartList();
        Path filePath = Path.of(baseDir, file);
        if (!Files.exists(filePath))
            System.out.printf("Не удалось обнаружить файла '%s'%n", file);
        else
            try {
                Arrays.stream(Files.readString(filePath)
                                .split("#"))
                        .filter(s -> !s.isBlank())
                        .map(ChartObject::readFromString)
                        .forEach(read::add);
            } catch (IOException e) {
                System.out.printf("Не удалось прочесть файл '%s': %s%n", file, e.getLocalizedMessage());
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


}
