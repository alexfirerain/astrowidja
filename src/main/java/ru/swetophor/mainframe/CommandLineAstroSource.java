package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;
import ru.swetophor.celestialmechanics.Chart;

import static ru.swetophor.mainframe.Application.*;
import static ru.swetophor.mainframe.Decorator.*;

public class CommandLineAstroSource implements AstroSource {


    static String extractHeadOrder(String input) {
        return input.substring(0, input.length() - 2).trim();
    }

    static String extractTailOrder(String input) {
        return input.substring(2).trim();
    }

    /**
     * Создаёт карту на основе юзерского ввода.
     * Предлагает ввести координаты в виде "градусы минуты секунды"
     * для каждой стандартной {@link AstraEntity АстроСущности}. Затем предлагает вводить
     * дополнительные {@link Astra астры} в виде "название градусы минуты секунды".
     * Пустой ввод означает пропуск астры или отказ от дополнительного ввода.
     *
     * @return {@link Chart одиночную карту}, созданную на основе ввода.
     */
    @Override
    public Chart getChartFromUserInput() {
        print("Название новой карты: ");
        Chart x = new Chart(mainShield.getUserInput());
        for (AstraEntity a : AstraEntity.values()) {
            print(a.name + ": ");
            String input = mainShield.getUserInput();
            if (input.isBlank())
                continue;
            x.addAstraFromString(a.name + " " + input);
            print();
        }
        print("Ввод дополнительных астр в формате 'название градусы минуты секунды'");
        String input = mainShield.getUserInput();
        while (!input.isBlank()) {
            x.addAstraFromString(input);
            input = mainShield.getUserInput();
        }
        return x;
    }

    @Override
    public void listsCycle() {
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
            input = mainShield.getUserInput();
            if (input == null || input.isBlank()) return;

            if (input.equals("=")) {
                printInAsterisk(chartRepository.listLibrary());

            } else if (input.equals("==")) {
                printInAsterisk(FileChartRepository.reportBaseContentExpanded());

            } else if (input.toLowerCase().startsWith("xxx") || input.toLowerCase().startsWith("ххх")) {
                String order = input.substring(3).trim();
                chartRepository.deleteFile(order);

            } else if (input.endsWith(">>")) {
                ChartList loadingList = chartRepository.findList(extractHeadOrder(input));
                if (loadingList == null || loadingList.isEmpty()) {     // TODO: write a confirmation general utility
                    System.out.println("список не найден или пуст");
                } else {
                    DESK.clear();
                    DESK.addAll(loadingList);
                    mainShield.displayDesk();
                }

            } else if (input.endsWith("->")) {
                DESK.addAll(chartRepository.findList(extractHeadOrder(input)));
                mainShield.displayDesk();

            } else if (input.startsWith(">>")) {
                chartRepository.dropListToFile(DESK, extractTailOrder(input));

            } else if (input.startsWith("->")) {
                chartRepository.saveTableToFile(DESK, extractTailOrder(input));
            }
        }

    }

    @Override
    public void loadFromFile(String filename) {
        chartRepository.readChartsFromBase(filename)
                .forEach(c -> Application.DESK.addResolving(c, "на столе"));
        print("Загружены карты из " + filename);
    }
}
