package ru.swetophor.mainframe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.swetophor.mainframe.Application.libraryService;
import static ru.swetophor.mainframe.Decorator.*;

/**
 * Предоставляет модель хранения данных в файлах попки базы данных.
 * Содержит список всех доступных карт и инструментарий по управлению картами.
 */
public class Storage {


    static void fullBaseReport() {
        System.out.println("В базе присутствуют следующие файлы и карты:");
        System.out.println(frameText(libraryService.exploreLibrary(), 40, '*'));
    }


    private static String[] getNames(String filename) throws IOException {
        return Files.readString(Path.of(FileChartRepository.baseDir, filename))
                .lines()
                .filter(line -> line.startsWith("#") && line.length() > 1)
                .map(line -> line.substring(1).strip())
                .toArray(String[]::new);
    }


}
