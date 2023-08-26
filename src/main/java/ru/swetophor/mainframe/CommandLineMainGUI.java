package ru.swetophor.mainframe;

import java.util.Scanner;

import static ru.swetophor.mainframe.Decorator.*;
import static ru.swetophor.mainframe.Settings.*;

public class CommandLineMainGUI implements MainGUI {

    static final Scanner KEYBOARD = new Scanner(System.in);


    @Override
    public void mainCycle() {
        Application.listCharts();
        String MENU = """
                1. карты на столе
                2. настройки
                3. списки карт
                4. работа с картой
                5. добавить карту с клавиатуры
                0. выход
                """;
        boolean exit = false;
        while (!exit) {
            printInDoubleFrame(MENU);
            switch (KEYBOARD.nextLine()) {
                case "1" -> Application.listCharts();
                case "2" -> Settings.editSettings();
                case "3" -> Storage.listsCycle();
                case "4" -> Application.takeChart();
                case "5" -> Application.addChart(Application.enterChartData());
                case "0" -> exit = true;
            }
        }
        print("Спасибо за ведание резонансов!");
    }

    @Override
    public void welcome() {
        System.out.printf("%sСчитаем резонансы с приближением в %.0f° (1/%d часть круга) до числа %d%n%n",
                asteriskFrame("Начато исполнение АстроВидьи!"),
                getPrimalOrb(), getOrbDivisor(), getEdgeHarmonic());
    }
}
