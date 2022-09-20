package ru.swetophor.НебеснаяМеханика;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static АстроВидья.НебеснаяМеханика.ЗнакЗодиака.зодийТочки;

/** * * * * * * * * * * * * * * * * * * * * *
 * Инструментальный класс для решения       *
 * задач по пространственному расположению  *
 * * * * * * * * * * * * * * * * * * * * *  */

public class Механика {
    public static final double КРУГ = 360.0;

    // вычисляет дугу между двумя точками на большом круге
    public static double получитьДугу(double а, double б){
        double дуга = abs(привестиКоординату(а) - привестиКоординату(б));
        if (дуга > КРУГ/2) дуга = КРУГ - дуга;
        return дуга;
    }

    // вычисляет дугу между астрами, переданными как объекты
    public static double получитьДугу(Астра а, Астра б){
        return получитьДугу(а.зодиакальнаяПозиция, б.зодиакальнаяПозиция);
    }

    // приводит дугу к расстоянию меж ея концами
    public static double привестиАспект(double а){
        return получитьДугу(привестиКоординату(а), 0);
    }

    // приводит координату в диапазон от 0 до 360°
    public static double привестиКоординату(double т){
        while (т < 0) т += КРУГ;
        т %= КРУГ;
        return т;
    }

//    public static String секундФормат(double вГрадусах) {
//        double минутнаяЧасть = вГрадусах % 1;
//        if (минутнаяЧасть < 1 / (КРУГ * 60)) return String.valueOf((int) вГрадусах) + "°";
//        else if (вГрадусах  %  (1.0/60) < 1 / (КРУГ * 3600)) {
//            return String.valueOf((int) вГрадусах) + "°" +
//                    String.valueOf((int) (минутнаяЧасть * 60) + "'");
//        }
//        else {
//            return String.valueOf((int) вГрадусах) + "°" +
//                    String.valueOf((int) ((минутнаяЧасть * 60)) + "'") +
//                    String.valueOf((int) floor(((минутнаяЧасть * 60) % 1) * 60)) + "\"";
//        }
//    }

    public static String секундФормат(double вГрадусах) {
        int вСекундах = (int) round((вГрадусах * 3600));
        int градусов = вСекундах / (3600);
        int минут = вСекундах % 3600 / 60;
        int секунд = вСекундах - градусов * 3600 - минут * 60;
        return format("%3s", valueOf(градусов)) + "°" + format("%2s", valueOf(минут)) + "'" + format("%2s", valueOf(секунд))+ "\"";
    }

    public static String секундФормат(double вГрадусах, boolean безЛишнихНолей) {
        /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** *
         * Выдаёт строку вида градусы°минуты'секунды"                         *
         *      на основе переданной дуги в десятичных градусах.              *
         * Если безЛишнихНолей = false,                                       *
         *      то отсутствующия секунды или секунды и минуты упускаются.     *
         ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** */
        int вСекундах = (int) round((вГрадусах * 3600));
        int градусов = вСекундах / 3600;
        int минут = вСекундах % 3600 / 60;
        int секунд = вСекундах - градусов * 3600 - минут * 60;
        String градусня = "";
        if (безЛишнихНолей && (градусов != 0)) градусня = valueOf(градусов) + "°";
//        else градусня = format("% 3d°", градусов);
        if (безЛишнихНолей && (минут > 0 || секунд > 0) && (минут != 0)) градусня += valueOf(минут) + "'";
//        else градусня += format("% 2d'", минут);
        if (безЛишнихНолей && секунд > 0) градусня += valueOf(секунд) + "\"";
//        else градусня += format("% 2d\"", секунд);
        if (градусня.length() == 0) градусня = "0°";
        return градусня;
    }

    public static String секундФорматТаблично(double вГрадусах, boolean безЛишнихНолей) {
        /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
         * Аналогично функции секундФормат(),                   *
         *      но выдаёт строку длиной точно 10 символов,      *
         *      выровненную влево.                              *
         ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/
        int вСекундах = (int) round((вГрадусах * 3600));
        int градусов = вСекундах / 3600;
        int минут = вСекундах % 3600 / 60;
        int секунд = вСекундах - градусов * 3600 - минут * 60;
        String форматка;
        if (безЛишнихНолей) форматка = format("%3s", valueOf(градусов)) + "°";
        else форматка = format("%03d°", градусов);
        if (безЛишнихНолей && (минут > 0 || секунд > 0)) форматка += format("%2s'", valueOf(минут));
        else форматка += format("%02d'", минут);
        if (безЛишнихНолей && секунд > 0) форматка += format("%2s\"", valueOf(секунд));
        else форматка += format("%02d\"", секунд);
        форматка = format("%-10s", форматка);
        return форматка;

    }    public static String секундФорматТаблично(double вГрадусах) {
        /** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
         *      Выдаёт строку точно 10 знаков                   *
         *          съ всеми избыточными нолями                 *
         ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **/
        int вСекундах = (int) round((вГрадусах * 3600));
        int градусов = вСекундах / 3600;
        int минут = вСекундах % 3600 / 60;
        int секунд = вСекундах - градусов * 3600 - минут * 60;
        String форматка;
        форматка = format("%03d°", градусов) + format("%02d'", минут) + format("%02d\"", секунд);
        форматка = format("%-10s", форматка);
        return форматка;
    }



    // преобразует эклиптическую долготу в зодиакальную
    public static String зодиакФормат(double позиция) {
        return зодийТочки(позиция) + "\t" + секундФормат(позиция % 30, false);
    }

    public static double найтиСредницу(double позицияА, double позицияБ) {
        double дуга = получитьДугу(позицияА, позицияБ);
        double меньшаяПозиция;
        if (привестиКоординату(позицияА + дуга) == позицияБ) меньшаяПозиция = позицияА;
        else меньшаяПозиция = позицияБ;
        double средница = привестиКоординату(меньшаяПозиция + дуга / 2);
        if (дуга == КРУГ / 2) средница = -средница;
        return средница;
    }
}
