package ru.swetophor;

import static АстроВидья.НебеснаяМеханика.Механика.КРУГ;

public class Настройки {
    static int крайняяГармоника = 108;
    static int делительОрбиса = 30;
    static boolean орбисПополамДляДвойных = true;

    public static int выводКрайнейГармоники() { return крайняяГармоника; }

    public static void вводКрайнейГармоники(int крайняяГармоника) { Настройки.крайняяГармоника = крайняяГармоника; }

    public static int выводДелителяОрбиса() { return делительОрбиса; }

    public static void вводДелителяОрбиса(int делительОрбиса) { Настройки.делительОрбиса = делительОрбиса; }

    public static double выводОрбиса() { return КРУГ / делительОрбиса; }

}
