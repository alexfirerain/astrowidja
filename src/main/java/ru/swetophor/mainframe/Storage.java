package ru.swetophor.mainframe;

import static ru.swetophor.mainframe.Application.libraryService;
import static ru.swetophor.mainframe.Decorator.*;

/**
 * Предоставляет модель хранения данных в файлах попки базы данных.
 * Содержит список всех доступных карт и инструментарий по управлению картами.
 */
public class Storage {


    static void fullBaseReport() {
        System.out.println("В базе присутствуют следующие файлы и карты:");
        System.out.println(frameText(libraryService.libraryListing(), 40, '*'));
    }


}
