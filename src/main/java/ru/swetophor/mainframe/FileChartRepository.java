package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static ru.swetophor.mainframe.Application.DESK;
import static ru.swetophor.mainframe.Application.mainShield;
import static ru.swetophor.mainframe.Decorator.print;
import static ru.swetophor.mainframe.Decorator.printInAsterisk;

public class FileChartRepository implements ChartRepository {
    /**
     * Прочитывает содержание всех файлов с картами.
     * Если по какой-то причине таковых не найдено, то пустой список.
     *
     * @return список списков карт, соответствующих файлам в рабочей папке.
     */
    static List<ChartList> scanLibrary() {
        return Storage.tableOfContents().stream()
                .map(FileChartRepository::readChartsFromFile)
                .toList();
    }

    public static String newAutosaveName() {
        return "сохранение %s.awb"
                .formatted(new SimpleDateFormat("E d MMMM .yy HH-mm")
                        .format(new Date()));
    }

    /**
     * Добавляет карты из указанного картосписка в файл с указанным именем.
     * Если список пуст или в ходе выполнения ни одной карты из списка не добавляется,
     * сообщает об этом и выходит. Если хотя бы одна карта добавляется,
     * переписывает указанный файл его новой версией после слияния и сообщает,
     * какое содержание было записано. Если запись обламывается, сообщает и об этом.
     * @param table список карт, который надо добавить к списку в файле.
     * @param target    имя файла в папке базы данных, в который нужно дописать карты.
     */
    @Override
    public void saveTableToFile(ChartList table, String target) {
        String result;
        ChartList fileContent = readChartsFromFile(target);
        if (table.isEmpty() || !fileContent.addAll(table)) {
            result = "Никаких новых карт в файл не добавлено.";
        } else {
            String drop = fileContent.getString();

            try (PrintWriter out = new PrintWriter(Path.of(Storage.baseDir, target).toFile())) {
                out.println(drop);
                result = "Строка%n%s%n записана в %s%n".formatted(drop, target);
            } catch (FileNotFoundException e) {
                result = "Запись в файл %s обломалась: %s%n".formatted(target, e.getLocalizedMessage());
            }
        }
        print(result);
    }

    public void moveChartsToFile(String source, String target, int... charts) {
        Storage.chartLibrary = scanLibrary();
        ChartList sourceList = Storage.chartLibrary.get(Storage.tableOfContents().indexOf(source));
        ChartList targetList = Storage.chartLibrary.get(Storage.tableOfContents().indexOf(target));

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

    @Override
    public void putChartToBase(ChartObject chart, String file) {
        ChartList fileContent = readChartsFromFile(file);
        if (fileContent.addResolving(chart, file))
            dropListToFile(fileContent, file);
    }

    public boolean  putChartsToBase(String file, ChartObject... charts) {
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
        if (file == null) {
            System.out.println("Файл не указан");
        }
        Path filePath = Path.of(Storage.baseDir, file);
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
     * @param fileName    имя файла в рабочей папке.
     */
    @Override
    public void dropListToFile(ChartList content, String fileName) {
        fileName = extendFileName(content, fileName);

        try (PrintWriter out = new PrintWriter(Path.of(Storage.baseDir, fileName).toFile())) {
            out.println(content.getString());
            System.out.printf("Карты {%s} записаны в файл %s.%n",
                    String.join(", ", content.getNames()),
                    fileName);
        } catch (FileNotFoundException e) {
            System.out.printf("Запись в файл %s обломалась: %s%n", fileName, e.getLocalizedMessage());
        }
    }

    private static String extendFileName(ChartList content, String file) {
        if (!file.endsWith(".awb") && !file.endsWith(".awc"))
            file += content.size() == 1 ? ".awc" : ".awb";
        return file;
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
            int index;
            List<String> tableOfContents = Storage.tableOfContents();
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

    public static void deleteFile(String fileToDelete) {
        try {
            List<String> fileList = Storage.tableOfContents();
            if (fileToDelete.matches("^\\d+")) {
                int indexToDelete;
                try {
                    indexToDelete = Integer.parseInt(fileToDelete) - 1;
                    if (indexToDelete < 0 || indexToDelete >= fileList.size()) {
                        print("в базе всего " + fileList.size() + "файлов");
                    } else {
                        String nameToDelete = fileList.get(indexToDelete);
                        if (confirmDeletion(nameToDelete)) {
                            if (!Files.deleteIfExists(Path.of(Storage.baseDir, nameToDelete)))
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
                            if (!Files.deleteIfExists(Path.of(Storage.baseDir, name))) {
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
                    if (Files.exists(Path.of(Storage.baseDir, fileToDelete + ".awc")))
                        fileToDelete = fileToDelete + ".awc";
                    else if (Files.exists(Path.of(Storage.baseDir, fileToDelete + ".awb")))
                        fileToDelete = fileToDelete + ".awb";
                }
                if (!Files.deleteIfExists(Path.of(Storage.baseDir, fileToDelete))) {
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
        return mainShield
                .confirmationAnswer("Точно удалить " + fileToDelete + "?");
    }

    @Override
    public void loadBase(String filename) {
        readChartsFromFile(filename)
                .forEach(c -> DESK.addResolving(c, "на столе"));
        print("Загружены карты из " + filename);
    }

    @Override
    public void autosave() {
        saveTableToFile(DESK, newAutosaveName());
    }
}
