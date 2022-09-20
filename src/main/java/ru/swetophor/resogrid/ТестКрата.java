package ru.swetophor.resogrid;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ТестКрата {
    public static void main(String[] args) throws Exception {
        BufferedReader ввод = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Работа тестократа:\n");
        while (true) {
            System.out.println("Введи гармонику:");
            гармоника = Integer.parseInt(ввод.readLine());
            if (гармоника <= 0) break;
            System.out.println("Введи дугу:");
            дуга = Double.parseDouble(ввод.readLine());
            if (дуга < 0.0) break;
            System.out.println("Введи зазор:");
            зазор = Double.parseDouble(ввод.readLine());
            if (зазор < 0) break;
            System.out.println("Крат аспекта вероятно " + findMultiplier(гармоника, дуга, зазор) + "\n");
        }
        System.out.println("\nРабота тестократа завершена!");
    }

    static int гармоника;
    static double дуга;
    static double зазор;

    static private int findMultiplier(int резонанс, double дуга, double зазор) {
        double единичник = 360. / резонанс;
        int крат = 1;
        while (крат <= резонанс / 2) {
            if (дуга == крат * единичник + зазор || дуга == крат * единичник - зазор)
                return крат;
            крат++;
        }
        return крат;
    }
}
