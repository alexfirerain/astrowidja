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
import static ru.swetophor.mainframe.Decorator.print;

public class FileChartRepository implements ChartRepository {
    static final String baseDir = "base";
    /**
     * Рабочая папка.
     */
    protected static File base = new File(baseDir);
    /**
     * Список списков карт, соответствующих файлам в рабочей папке.
     */
    protected List<ChartList> chartLibrary = getWholeLibrary();

    static {
        Path basePath = Path.of(baseDir);
        if (!Files.exists(basePath)) {
            String msg;
            try {
                Files.createDirectory(basePath);
                msg = "Создали папку '%s'%n".formatted(baseDir);
            } catch (IOException e) {
                msg = "Не удалось создать папку %s: %s%n".formatted(baseDir, e.getLocalizedMessage());
            }
            print(msg);
        }
    }

    private static String[] getNames(String filename) throws IOException {
        return Files.readString(Path.of(baseDir, filename))
                .lines()
                .filter(line -> line.startsWith("#") && line.length() > 1)
                .map(line -> line.substring(1).strip())
                .toArray(String[]::new);
    }

    /**
     * Прочитывает и отдаёт список файлов в рабочей папке.
     * Вспомогательный метод при файловой реализации картохранилища.
     *
     * @return список имён файлов АстроВидьи *.awb и *.awc, присутствующих в рабочей папке
     * в момент вызова, сортированный по дате последнего изменения, где последними самые свежеизменённые.
     * Если путь к базе не определён, или её файл не существует или не является папкой, то пустой список.
     */
    private List<File> getBaseContent() {
        return base == null || !base.exists() || base.listFiles() == null ?
                new ArrayList<>() :
                Arrays.stream(Objects.requireNonNull(base.listFiles()))
                        .filter(file -> !file.isDirectory())
                        .filter(file -> file.getName().endsWith(".awb") || file.getName().endsWith(".awc"))
                        .sorted(Comparator.comparing(File::lastModified))
                        .collect(Collectors.toList());
    }

    /**
     * Выдаёт строковое представление содержимого рабочей папки.
     *
     * @return нумерованный (начиная с 1) построчный список
     * файлов *.awb и *.awc в рабочей папке, сортированный по дате изменения.
     */
    @Override
    public String listLibrary() {   // TODO: превратить в декоратор, принимающий вывод baseNames()
        List<String> names = baseNames();

        return IntStream.range(0, names.size())
                .mapToObj(i -> "%d. %s%n"
                        .formatted(i + 1, names.get(i)))
                .collect(Collectors.joining());
    }

    /**
     * Выдаёт список баз (списков карт), присутствующих в картохранилище.
     *
     * @return список имён файлов АстроВидьи, присутствующих в рабочей папке
     * в момент вызова, сортированный по дате последнего изменения.
     */
    @Override
    public List<String> baseNames() {
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
    @Override
    public List<ChartList> getWholeLibrary() {
        return baseNames().stream()
                .map(this::readChartsFromBase)
                .toList();
    }

    public static String newAutosaveName() {
        return "сохранение %s.awb"
                .formatted(new SimpleDateFormat("E d MMMM .yy HH-mm")
                        .format(new Date()));
    }

    public String showList(String order) {
        return findList(order).toString();
    }

    /**
     * Добавляет карты из указанного картосписка в файл с указанным именем.
     * Если список пуст или в ходе выполнения ни одной карты из списка не добавляется,
     * сообщает об этом и выходит. Если хотя бы одна карта добавляется,
     * переписывает указанный файл его новой версией после слияния и сообщает,
     * какое содержание было записано.
     * Если запись обламывается, сообщает и об этом.
     *
     * @param table  список карт, который надо добавить к списку в файле.
     * @param target имя файла в папке базы данных, в который нужно дописать карты.
     * @return  строку с описанием результата операции.
     */
    @Override
    public String addChartsToGroup(ChartList table, String target) {
        String result;
        ChartList fileContent = readChartsFromBase(target);
        if (table.isEmpty() || !fileContent.addAll(table)) {
            result = "Никаких новых карт в файл не добавлено.";
        } else {
            String drop = fileContent.getString();

            try (PrintWriter out = new PrintWriter(Path.of(baseDir, target).toFile())) {
                out.println(drop);
                result = "Строка {%n%s%n} записана в %s%n".formatted(drop, target);
            } catch (FileNotFoundException e) {
                result = "Запись в файл %s обломалась: %s%n".formatted(target, e.getLocalizedMessage());
            }
        }
        return result;
    }

    public void moveChartsToFile(String source, String target, int... charts) {
        chartLibrary = getWholeLibrary();
        ChartList sourceList = chartLibrary.get(baseNames().indexOf(source));
        ChartList targetList = chartLibrary.get(baseNames().indexOf(target));

        Arrays.stream(charts)
                .mapToObj(sourceList::get)
                .toList()
                .stream()
                .filter(c -> targetList.addResolving(c, target))
                .map(ChartObject::getName)
                .forEach(sourceList::remove);

        if (!sourceList.equals(readChartsFromBase(source)))
            saveChartsAsGroup(sourceList, source);
        if (!targetList.equals(readChartsFromBase(target)))
            saveChartsAsGroup(targetList, target);
    }

    @Override
    public boolean  putChartsToBase(String file, ChartObject... charts) {
        ChartList fileContent = readChartsFromBase(file);
        boolean changed = false;
        for (ChartObject c : charts)
            if (fileContent.addResolving(c, file))
                changed = true;
        if (changed)
            saveChartsAsGroup(fileContent, file);
        return changed;
    }

    /**
     * Прочитывает список карт из формата *.awb
     *  Если файл не существует или чтение обламывается,
     *  выводит об этом сообщение
     * @param fileName имя файла в папке данных.
     * @return список карт, прочитанных из файла.
     * Если файл не существует или не читается, то пустой список.
     */
    @Override
    public ChartList readChartsFromBase(String fileName) {
        ChartList read = new ChartList();
        if (fileName == null) {
            System.out.println("Файл не указан");
        }
        Path filePath = Path.of(baseDir, fileName);
        if (!Files.exists(filePath))
            System.out.printf("Не удалось обнаружить файла '%s'%n", fileName);
        else
            try {
                Arrays.stream(Files.readString(filePath)
                                .split("#"))
                        .filter(s -> !s.isBlank())
                        .map(ChartObject::readFromString)
                        .forEach(chart -> read.addResolving(chart, fileName));
            } catch (IOException e) {
                System.out.printf("Не удалось прочесть файл '%s': %s%n", fileName, e.getLocalizedMessage());
            }
        return read;
    }

    /**
     * Записывает содержимое картосписка (как возвращается {@link ChartList#getString()})
     * в файл по указанному адресу (относительно рабочей папки).
     * Существующий файл заменяется, несуществующий создаётся.
     *
     * @param content список карт, чьё содержимое записывается.
     * @param fileName    имя файла в рабочей папке.
     */
    @Override
    public void saveChartsAsGroup(ChartList content, String fileName) {
        fileName = extendFileName(fileName, content.size() == 1);

        try (PrintWriter out = new PrintWriter(Path.of(baseDir, fileName).toFile())) {
            out.println(content.getString());
            System.out.printf("Карты {%s} записаны в файл %s.%n",
                    String.join(", ", content.getNames()),
                    fileName);
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", fileName, e.getLocalizedMessage());
        }
    }

    private static String extendFileName(String filename, boolean asAwc) {
        if (!filename.endsWith(".awb") && !filename.endsWith(".awc"))
            filename += asAwc ? ".awc" : ".awb";
        return filename;
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
    public ChartList findList(String order) {
        ChartList list = new ChartList();
        if (order == null || order.isBlank())
            return list;
        List<ChartList> base = getWholeLibrary();

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
            int index;
            List<String> tableOfContents = baseNames();
            index = IntStream.range(0, tableOfContents.size())
                    .filter(i -> tableOfContents.get(i).startsWith(order))
                    .findFirst()
                    .orElse(-1);
            if (index == -1)
                System.out.println("Нет файла с именем на " + order);
            else
                list = base.get(index);
        }
        return list;
    }

    @Override
    public String deleteFile(String fileToDelete) {
        // TODO: выделить функции идентификации файла/карты по номеру/имени
        String report = "Файл %s удалён.".formatted(fileToDelete);
        try {
            List<String> fileList = baseNames();
            if (fileToDelete.matches("^\\d+")) {
                int indexToDelete;
                try {
                    indexToDelete = Integer.parseInt(fileToDelete) - 1;
                    if (indexToDelete < 0 || indexToDelete >= fileList.size()) {
                        report = "в базе всего " + fileList.size() + "файлов";
                    } else {
                        String nameToDelete = fileList.get(indexToDelete);
                        if (confirmDeletion(nameToDelete)) {
                            if (!Files.deleteIfExists(Path.of(baseDir, nameToDelete))) {
                                report = "не найдено файла " + nameToDelete;
                            }
                        } else {
                            report ="отмена удаления " + nameToDelete;
                        }
                    }
                } catch (NumberFormatException e) {         // никогда не выбрасывается
                    report = "не разобрать числа, " + e.getLocalizedMessage();
                }
            } else if (fileToDelete.endsWith("***")) {
                String prefix = fileToDelete.substring(0, fileToDelete.length() - 3);
                for (String name : fileList) {
                    if (name.startsWith(prefix)) {
                        if (confirmDeletion(name)) {
                            if (!Files.deleteIfExists(Path.of(baseDir, name))) {
                                report = "не найдено файла " + name;
                            } else {
                                report = name + " удалился";
                            }
                        } else {
                            report = "отмена удаления " + name;
                        }
                    }
                }
            } else if (fileToDelete.matches("^[\\p{L}\\-. !()+=_\\[\\]№\\d]+$")) {
                // TODO: нормальную маску допустимого имени файла
                if (!fileToDelete.endsWith(".awb") && !fileToDelete.endsWith(".awc")) {
                    if (Files.exists(Path.of(baseDir, fileToDelete + ".awc"))) {
                        fileToDelete = fileToDelete + ".awc";
                    }
                    else if (Files.exists(Path.of(baseDir, fileToDelete + ".awb"))) {
                        fileToDelete = fileToDelete + ".awb";
                    }
                }
                if (!Files.deleteIfExists(Path.of(baseDir, fileToDelete))) {
                    report = "не найдено файла " + fileToDelete;
                }
            } else {
                report = "скорее всего, недопустимое имя файла";
            }
        } catch (IOException e) {
            report = "ошибка чтения базы, %s".formatted(e.getLocalizedMessage());
        } catch (Exception e) {
            report = "ошибка удаления файла %s: %s".formatted(fileToDelete, e.getLocalizedMessage());
        }
        return report;
    }

    private static boolean confirmDeletion(String fileToDelete) {
        return mainShield
                .confirmationAnswer("Точно удалить " + fileToDelete + "?");
    }

    @Override
    public String loadBase(String filename) {
        readChartsFromBase(filename)
                .forEach(c -> DESK.addResolving(c, "на столе"));
        return "Загружены карты из " + filename;
    }

    @Override
    public String autosave() {
        return addChartsToGroup(DESK, newAutosaveName());
    }

//    /**
//     * Находит в этом списке карту, заданную по имени или номеру в списке (начинающемуся с 1).
//     * Если запрос состоит только из цифр, рассматривает его как запрос по номеру,
//     * иначе как запрос по имени.
//     * @param order запрос, какую карту ищем в списке: по имени или номеру (с 1).
//     * @param inList    строка, описывающая этот список в местном падеже.
//     * @return  найденный в списке объект, соответствующий запросу.
//     * @throws ChartNotFoundException   если в списке не найдено соответствующих запросу объектов.
//     */
//    public ChartObject findChart(ChartList list, String order, String inList) throws ChartNotFoundException {
//        if (order == null || order.isBlank())
//            throw new ChartNotFoundException("Пустой запрос.");
//        if (!inList.startsWith("на "))
//            inList = "в " + inList;
//
//        if (order.matches("^\\d+"))
//            try {
//                int i = Integer.parseInt(order) - 1;
//                if (i >= 0 && i < list.size())
//                    return list.get(i);
//                else
//                    throw new ChartNotFoundException("Всего %d карт %s%n"
//                            .formatted(list.size(), inList));
//            } catch (NumberFormatException e) {
//                throw new ChartNotFoundException("Число не распознано.");
//            }
//        else if (list.contains(order)) {
//            return list.get(order);
//        } else {
//            for (String name : list.getNames())
//                if (name.startsWith(order))
//                    return list.get(name);
//
//            throw new ChartNotFoundException("Карты '%s' нет %s%n"
//                    .formatted(order, inList));
//        }
//    }

}
