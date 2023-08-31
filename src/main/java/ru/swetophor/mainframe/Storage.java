package ru.swetophor.mainframe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.mainframe.Application.chartRepository;
import static ru.swetophor.mainframe.Decorator.*;

/**
 * Предоставляет модель хранения данных в файлах попки базы данных.
 * Содержит список всех доступных карт и инструментарий по управлению картами.
 */
public class Storage {


    /**
     * Рабочая папка.
     */
    protected static File base = new File(FileChartRepository.baseDir);
    /**
     * Список списков карт, соответствующих файлам в рабочей папке.
     */
    protected static List<ChartList> chartLibrary = chartRepository.scanLibrary();

    /**
     * Выдаёт строковое представление содержимого рабочей папки.
     *
     * @return нумерованный (начиная с 1) построчный список
     * файлов *.awb и *.awc в рабочей папке, сортированный по дате изменения.
     */
    public static String listLibrary() {
        List<String> names = chartRepository.tableOfContents();

        return IntStream.range(0, names.size())
                .mapToObj(i -> "%d. %s%n"
                        .formatted(i + 1, names.get(i)))
                .collect(Collectors.joining());
    }

    static void fullBaseReport() {
        System.out.println("В базе присутствуют следующие файлы и карты:");
        System.out.println(frameText(reportBaseContentExpanded(), 40, '*'));
    }

    /**
     * Прочитывает и отдаёт список файлов в рабочей папке.
     *
     * @return список файлов *.awb и *.awc в папке базы. Если путь к базе не определён,
     * или её файл не существует или не является папкой, то пустой список.
     * Файлы в списке сортируются по дате изменения.
     */
    static List<File> getBaseContent() {
        return base == null || !base.exists() || base.listFiles() == null ?
                new ArrayList<>() :
                Arrays.stream(Objects.requireNonNull(base.listFiles()))
                        .filter(file -> !file.isDirectory())
                        .filter(file -> file.getName().endsWith(".awb") || file.getName().endsWith(".awc"))
                        .sorted(Comparator.comparing(File::lastModified))
                        .collect(Collectors.toList());
    }


    public static String reportBaseContentExpanded() {
        chartLibrary = chartRepository.scanLibrary();

        StringBuilder output = new StringBuilder();
        List<String> tableOfContents = chartRepository.tableOfContents();

        for (int f = 0; f < tableOfContents.size(); f++) {
            String filename = tableOfContents.get(f);
            output.append("%d. ".formatted(f + 1)).append(filename).append(":\n");
            List<String> chartNames = chartLibrary.get(f).getNames();

            if (filename.endsWith(".awb"))
                IntStream.range(0, chartNames.size())
                        .mapToObj(i -> "\t%3d. %s%n"
                                .formatted(i + 1, chartNames.get(i)))
                        .forEach(output::append);

            if (filename.endsWith(".awc") && !chartNames.isEmpty())
                output.append(chartNames.get(0));

        }

        return output.toString();
    }

    private static String[] getNames(String filename) throws IOException {
        return Files.readString(Path.of(FileChartRepository.baseDir, filename))
                .lines()
                .filter(line -> line.startsWith("#") && line.length() > 1)
                .map(line -> line.substring(1).strip())
                .toArray(String[]::new);
    }


    static String extractHeadOrder(String input) {
        return input.substring(0, input.length() - 2).trim();
    }
    static String extractTailOrder(String input) {
        return input.substring(2).trim();
    }
}
