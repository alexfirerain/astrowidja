package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

import static ru.swetophor.mainframe.Application.DESK;
import static ru.swetophor.mainframe.Decorator.print;

public interface MainUI {

    /**
     * Основное меню и основной рабочий цикл АстроВидьи.
     */
    void mainCycle();

    /**
     * Выводит сообщение при старте Астровидьи.
     */
    void welcome();

    String getUserInput();

    void displayDesk();

    /**
     * Добавляет карту на {@link Application#DESK стол}.
     * Если карта с таким именем уже
     * присутствует, запрашивает решение у юзера.
     *
     * @param chart добавляемая карта.
     * @return  строку, сообщающую состояние операции.
     */
    default String addChart(ChartObject chart) {
        if (mergeResolving(DESK, chart, "на столе"))
            return "Карта загружена на стол: " + chart;
        else
            return "Карта не загружена.";
    }

    void workCycle(ChartObject chartObject);

    boolean confirmationAnswer(String prompt);

    boolean mergeResolving(ChartList list, ChartObject nextChart, String listName);
}
