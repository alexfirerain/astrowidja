package ru.swetophor.mainframe;

import ru.swetophor.celestialmechanics.ChartObject;

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

    void listCharts();

    void workCycle(ChartObject chartObject);

    boolean confirmationAnswer(String s);
}
