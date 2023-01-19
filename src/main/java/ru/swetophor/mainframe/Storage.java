package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.ChartObject;
import ru.swetophor.celestialmechanics.ChartType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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


    static void manageCharts() {
        System.out.println("В базе присутствуют следующие файлы и карты:");

        String content = reportBasesContent();
        if (content == null)
            content = "Не удалось получить содержимое базы.";

        System.out.println(Decorator.frameText(content, 40, '*'));

    }

    public static void saveTableToFile(Map<String, ChartObject> table, String target) {
        StringBuilder content = new StringBuilder();
        table.values().stream()
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
