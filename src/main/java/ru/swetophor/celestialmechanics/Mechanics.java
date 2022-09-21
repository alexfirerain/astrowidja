package ru.swetophor.celestialmechanics;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.String.format;
import static ru.swetophor.celestialmechanics.ZodiacSign.pointsZodium;

/**
 * Инструментальный класс для решения
 * задач по пространственному расположению
 */
public class Mechanics {
    public static final double CIRCLE = 360.0;

    // вычисляет дугу между двумя точками на большом круге
    public static double getArc(double a, double b){
        double arc = abs(normalizeCoordinate(a) - normalizeCoordinate(b));
        if (arc > CIRCLE /  2)
            arc = CIRCLE - arc;
        return arc;
    }

    // вычисляет дугу между астрами, переданными как объекты
    public static double getArc(Astra a, Astra b){
        return getArc(a.getZodiacPosition(), b.getZodiacPosition());
    }

    // приводит дугу к расстоянию меж ея концами
    public static double normalizeArc(double a){
        return getArc(normalizeCoordinate(a), 0);
    }

/**
    Приводит координату в диапазон от 0 до 360°.
*/
        public static double normalizeCoordinate(double p){
            p %= CIRCLE;
            if (p < 0)
                p += CIRCLE;
        return p;
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

    public static String secondFormat(double inDegrees) {
        int inSeconds = (int) round((inDegrees * 3600));
        int degrees = inSeconds / (3600);
        int minutes = inSeconds % 3600 / 60;
        int seconds = inSeconds - degrees * 3600 - minutes * 60;
        return format("%3s", degrees) + "°" + format("%2s", minutes) + "'" + format("%2s", seconds)+ "\"";
    }

    /**
     * Выдаёт строку вида градусы°минуты'секунды"
     *      на основе переданной дуги в десятичных градусах.
     * Если withoutExtraZeros = false,
     *      то отсутствующия секунды или секунды и минуты упускаются.
     */
    public static String secondFormat(double inDegrees, boolean withoutExtraZeros) {
        int inSeconds = (int) round((inDegrees * 3600));
        int degrees = inSeconds / 3600;
        int minutes = inSeconds % 3600 / 60;
        int seconds = inSeconds - degrees * 3600 - minutes * 60;
        String degreeString = "";
        if (withoutExtraZeros && (degrees != 0)) degreeString = degrees + "°";
//        else degreeString = format("% 3d°", градусов);
        if (withoutExtraZeros && (minutes > 0 || seconds > 0) && (minutes != 0)) degreeString += minutes + "'";
//        else degreeString += format("% 2d'", минут);
        if (withoutExtraZeros && seconds > 0) degreeString += seconds + "\"";
//        else degreeString += format("% 2d\"", секунд);
        if (degreeString.length() == 0) degreeString = "0°";
        return degreeString;
    }

    /**
     * Аналогично функции секундФормат(),
     *      но выдаёт строку длиной точно 10 символов,
     *      выровненную влево.
     */
    public static String secondFormatTablewise(double inDegrees, boolean withoutExtraZeros) {

        int inSeconds = (int) round((inDegrees * 3600));
        int degrees = inSeconds / 3600;
        int minutes = inSeconds % 3600 / 60;
        int seconds = inSeconds - degrees * 3600 - minutes * 60;
        String formatHolder;
        if (withoutExtraZeros)
            formatHolder = format("%3s", degrees) + "°";
        else
            formatHolder = format("%03d°", degrees);
        if (withoutExtraZeros && (minutes > 0 || seconds > 0))
            formatHolder += format("%2s'", minutes);
        else
            formatHolder += format("%02d'", minutes);
        if (withoutExtraZeros && seconds > 0)
            formatHolder += format("%2s\"", seconds);
        else
            formatHolder += format("%02d\"", seconds);

        return format("%-10s", formatHolder);

    }

    /**
     *      Выдаёт строку точно 10 знаков
     *          съ всеми избыточными нолями
     */
    public static String secondFormatTablewise(double inDegrees) {
        int inSeconds = (int) round((inDegrees * 3600));
        int degrees = inSeconds / 3600;
        int minutes = inSeconds % 3600 / 60;
        int seconds = inSeconds - degrees * 3600 - minutes * 60;
        String formatHolder;
        formatHolder = format("%03d°", degrees) + format("%02d'", minutes) + format("%02d\"", seconds);
        formatHolder = format("%-10s", formatHolder);
        return formatHolder;
    }


    /**
     преобразует эклиптическую долготу в зодиакальную
     */
    public static String зодиакФормат(double позиция) {
        return pointsZodium(позиция) + "\t" + secondFormat(позиция % 30, false);
    }

    public static double findMedian(double positionA, double positionB) {
        double arc = getArc(positionA, positionB);
        double minorPosition;
        if (normalizeCoordinate(positionA + arc) == positionB)
            minorPosition = positionA;
        else
            minorPosition = positionB;

        double median = normalizeCoordinate(minorPosition + arc / 2);

        if (arc == CIRCLE / 2)
            median = -median;
        return median;
    }
}
