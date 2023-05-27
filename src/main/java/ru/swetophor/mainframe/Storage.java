package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.mainframe.Application.*;
import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Decorator.printInAsterisk;
import static ru.swetophor.mainframe.Decorator.printInSemiDouble;

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
     * Список списков карт, соответствующих файлам в рабочей папке.
     */
    protected static List<ChartList> chartLibrary = scanLibrary();

    /**
     * Прочитывает список файлов из рабочей папки.
     *
     * @return список имён файлов АстроВидьи, присутствующих в рабочей папке
     * в момент вызова, сортированный по дате последнего изменения.
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

    /**
     * Выдаёт строковое представление содержимого рабочей папки.
     *
     * @return нумерованный (начиная с 1) построчный список
     * файлов *.awb и *.awc в рабочей папке, сортированный по дате изменения.
     */
    public static String listLibrary() {
        List<String> names = tableOfContents();

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
    private static List<File> getBaseContent() {
        return base == null || !base.exists() || base.listFiles() == null ?
                new ArrayList<>() :
                Arrays.stream(Objects.requireNonNull(base.listFiles()))
                        .filter(file -> !file.isDirectory())
                        .filter(file -> file.getName().endsWith(".awb") || file.getName().endsWith(".awc"))
                        .sorted(Comparator.comparing(File::lastModified))
                        .collect(Collectors.toList());
    }


    public static String reportBaseContentExpanded() {
        chartLibrary = scanLibrary();

        StringBuilder output = new StringBuilder();
        List<String> tableOfContents = tableOfContents();

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
        return Files.readString(Path.of(baseDir, filename))
                .lines()
                .filter(line -> line.startsWith("#") && line.length() > 1)
                .map(line -> line.substring(1))
                .toArray(String[]::new);
    }

    public static void saveTableToFile(ChartList table, String target) {
        ChartList fileContent = readChartsFromFile(target);
        if (!fileContent.addAll(table)) {
            System.out.println("Никаких новых карт в файл не добавлено.");
            return;
        }
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
        if (fileContent.addResolving(chart, file))
            dropListToFile(fileContent, file);
    }
    public static boolean  putChartsToBase(String file, ChartObject... charts) {
        ChartList fileContent = readChartsFromFile(file);
        boolean changed = false;
        for (ChartObject c : charts)
            if (fileContent.addResolving(c, file))
                changed = true;
        if (changed)
            dropListToFile(fileContent, file);
        return changed;
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
                        .forEach(chart -> read.addResolving(chart, file));
            } catch (IOException e) {
                System.out.printf("Не удалось прочесть файл '%s': %s%n", file, e.getLocalizedMessage());
            }
        return read;
    }

    /**
     * Записывает содержимое картосписка (как возвращается {@link ChartList#getString()})
     * в файл по указанному адресу (относительно рабочей папки).
     * Существующий файл заменяется, несуществующий создаётся.
     *
     * @param content список карт, чьё содержимое записывается.
     * @param file    имя файла в рабочей папке.
     */
    private static void dropListToFile(ChartList content, String file) {
        if (!file.endsWith(".awb") && !file.endsWith(".awc"))
            file += content.size() == 1 ? ".awc" : ".awb";

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
     * Находит и возвращает список карт, соответствующий файлу в рабочей папке,
     * по имени файла или по его номеру в списке, предоставляемом {@link #listLibrary()}
     *
     * @param order строка запроса (имя или часть имени файла или целое число).
     * @return если аргумент соответствует натуральному числу, не превышающему количество
     * файлов с картами в рабочей папке, то список карт из файла с таким номером
     * (файлы сортированы по дате изменения).
     * В прочих случаях — список из первого найденного
     * файла с именем, начинающимся со строки, переданной как аргумент.
     * Если ни одним из способов файл не найден, то пустой список.
     */
    public static ChartList findList(String order) {
        ChartList list = new ChartList();
        if (order == null || order.isBlank())
            return list;
        List<ChartList> base = scanLibrary();

        if (order.matches("^\\d+"))
            try {
                int i = Integer.parseInt(order) - 1;
                if (i >= 0 && i < base.size())
                    list = base.get(i);
                else
                    System.out.printf("В базе всего %d файлов%n", base.size());
            } catch (NumberFormatException e) {
                System.out.println("Число не распознано.");
            }
        else {
            int index = -1;
            List<String> tableOfContents = tableOfContents();
            for (int i = 0; i < tableOfContents.size(); i++)
                if (tableOfContents.get(i).startsWith(order)) {
                    index = i;
                    break;
                }
            if (index == -1)
                System.out.println("Нет файла с именем на " + order);
            else
                list = base.get(index);
        }
        return list;
    }


    public static String showList(String order) {
        return findList(order).toString();
    }

    public static void deleteFile(String fileToDelete) {
        try {
            List<String> fileList = tableOfContents();
            if (fileToDelete.matches("^\\d+")) {
                int indexToDelete;
                try {
                    indexToDelete = Integer.parseInt(fileToDelete) - 1;
                    if (indexToDelete < 0 || indexToDelete >= fileList.size()) {
                        print("в базе всего " + fileList.size() + "файлов");
                    } else {
                        String nameToDelete = fileList.get(indexToDelete);
                        if (confirmDeletion(nameToDelete)) {
                            if (!Files.deleteIfExists(Path.of(baseDir, nameToDelete)))
                                print("не найдено файла " + nameToDelete);
                        } else
                            print("отмена удаления " + nameToDelete);
                    }
                } catch (NumberFormatException e) {
                    print("не разобрать числа, " + e.getLocalizedMessage());
                }
            } else if (fileToDelete.endsWith("***")) {
                String prefix = fileToDelete.substring(0, fileToDelete.length() - 3);
                for (String name : fileList) {
                    if (name.startsWith(prefix)) {
                        if (confirmDeletion(name)) {
                            if (!Files.deleteIfExists(Path.of(baseDir, name))) {
                                print("не найдено файла " + name);
                            } else {
                                print(name + " удалился");
                            }
                        } else {
                            print("отмена удаления " + name);
                        }
                    }
                }
            } else if (fileToDelete.matches("^[\\p{L}\\-. !()+=_\\[\\]№\\d]+$")) {
                // TODO: нормальную маску допустимого имени файла
                if (!fileToDelete.endsWith(".awb") && !fileToDelete.endsWith(".awc")) {
                    if (Files.exists(Path.of(baseDir, fileToDelete + ".awc")))
                        fileToDelete = fileToDelete + ".awc";
                    else if (Files.exists(Path.of(baseDir, fileToDelete + ".awb")))
                        fileToDelete = fileToDelete + ".awb";
                }
                if (!Files.deleteIfExists(Path.of(baseDir, fileToDelete))) {
                    print("не найдено файла " + fileToDelete);
                }
            } else {
                print("скорее всего, недопустимое имя файла");
            }
        } catch (IOException e) {
            printInAsterisk("ошибка чтения базы, " + e.getLocalizedMessage());
        } catch (Exception e) {
            printInAsterisk("ошибка удаления файла " + fileToDelete + ": " + e.getLocalizedMessage());
        }
    }

    private static boolean confirmDeletion(String fileToDelete) {
        printInFrame("Точно удалить " + fileToDelete + "?");
        return Settings.yesValues.contains(KEYBOARD.nextLine().toLowerCase());
    }

    public static String autosaveName() {
        return "сохранение %s.awb"
                .formatted(new SimpleDateFormat("E d MMMM .yy HH-mm")
                        .format(new Date()));
    }

    protected static void listsCycle() {
        String LIST_MENU = """
                ("список" — список по номеру или имени,
                 "карты" — карты по номеру или имени через пробел)
                    =               = список файлов в базе
                    ==              = полный список файлов и карт
                    ххх список      = удалить файл
                    
                    список >>       = заменить стол на список
                    список ->       = добавить список ко столу
                    >> список       = заменить файл столом
                    -> список       = добавить стол к списку
                    
                    карты -> список         = добавить карты со стола к списку
                    список:карты -> список  = переместить карты из списка в список
                    список:карты +> список  = копировать карты из списка в список
                """;
        printInSemiDouble(LIST_MENU);
        String input;
        while (true) {
            input = KEYBOARD.nextLine();
            if (input == null || input.isBlank()) return;

            if (input.equals("=")) {
                printInAsterisk(listLibrary());

            } else if (input.equals("==")) {
                printInAsterisk(reportBaseContentExpanded());

            } else if (input.toLowerCase().startsWith("xxx") || input.toLowerCase().startsWith("ххх")) {
                String order = input.substring(3).trim();
                deleteFile(order);

            } else if (input.endsWith(">>")) {
                ChartList loadingList = findList(extractHeadOrder(input));
                if (loadingList == null || loadingList.isEmpty()) {     // TODO: write a confirmation general utility
                    System.out.println("список не найден или пуст");
                } else {
                    DESK.clear();
                    DESK.addAll(loadingList);
                    listCharts();
                }

            } else if (input.endsWith("->")) {
                DESK.addAll(findList(extractHeadOrder(input)));
                listCharts();

            } else if (input.startsWith(">>")) {
                dropListToFile(DESK, extractTailOrder(input));

            } else if (input.startsWith("->")) {
                saveTableToFile(DESK, extractTailOrder(input));
            }
        }

    }
    private static String extractHeadOrder(String input) {
        return input.substring(0, input.length() - 2).trim();
    }
    private static String extractTailOrder(String input) {
        return input.substring(2).trim();
    }
}
