package ru.swetophor.celestialmechanics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    /**
     * вычисляет дугу между двумя точками на большом круге
     */
    public static double getArc(double a, double b){
        double arc = abs(normalizeCoordinate(a) - normalizeCoordinate(b));
        return arc > CIRCLE / 2 ?
                CIRCLE - arc :
                arc;
    }

    /**
     * вычисляет дугу между астрами, переданными как объекты
     */
    public static double getArc(Astra a, Astra b){
        return getArc(a.getZodiacPosition(), b.getZodiacPosition());
    }

    /**
     * приводит дугу к расстоянию меж ея концами
     */
    public static double normalizeArc(double a){
        return getArc(normalizeCoordinate(a), 0);
    }

/**
    Приводит координату в диапазон от 0 до 360°.
*/
    public static double normalizeCoordinate(double p){
        p %= CIRCLE;
        return p < 0 ? p + CIRCLE : p;
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
        int[] coors = degreesToCoors(inDegrees);
        return "%s°%s'%s\""
                .formatted(
                        format("%3s", coors[0]),
                        format("%2s", coors[1]),
                        format("%2s", coors[2])
                );
    }

    /**
     * Выдаёт строку вида градусы°минуты'секунды"
     *      на основе переданной дуги в десятичных градусах.
     * Если withoutExtraZeros = false,
     *      то отсутствующия секунды или секунды и минуты упускаются.
     */
    public static String secondFormat(double inDegrees, boolean withoutExtraZeros) {
        int[] coors = degreesToCoors(inDegrees);
        StringBuilder degreeString = new StringBuilder();

        if (withoutExtraZeros &&
                (coors[0] != 0))
            degreeString.append(coors[0]).append("°");
        else
          degreeString.append(format("% 3d°", coors[0]));

        if (withoutExtraZeros &&
                (coors[1] > 0 || coors[2] > 0) &&
                (coors[1] != 0))
            degreeString.append(coors[1]).append("'");
        else
          degreeString.append(format("% 2d'", coors[1]));

        if (withoutExtraZeros &&
                coors[2] > 0)
            degreeString.append(coors[2]).append("\"");
        else
            degreeString.append(format("% 2d\"", coors[2]));

        if (degreeString.length() == 0)
            return "0°";

        return degreeString.toString();
    }

    /**
     * Аналогично функции секундФормат(),
     *      но выдаёт строку длиной точно 10 символов,
     *      выровненную влево.
     */
    public static String secondFormatTablewise(double inDegrees, boolean withoutExtraZeros) {
        int[] coors = degreesToCoors(inDegrees);
        StringBuilder formatHolder = new StringBuilder();

        if (withoutExtraZeros)
            formatHolder.append(format("%3s°", coors[0]));
        else
            formatHolder.append(format("%03d°", coors[0]));

        if (withoutExtraZeros &&
                (coors[1] > 0 || coors[2] > 0))
            formatHolder.append(format("%2s'", coors[1]));
        else
            formatHolder.append(format("%02d'", coors[1]));

        if (withoutExtraZeros &&
                coors[2] > 0)
            formatHolder.append(format("%2s\"", coors[2]));
        else
            formatHolder.append(format("%02d\"", coors[2]));

        return format("%-10s", formatHolder);

    }

    /**
     *      Выдаёт строку точно 10 знаков
     *          съ всеми избыточными нолями
     */
    public static String secondFormatTablewise(double inDegrees) {
        int[] coors = degreesToCoors(inDegrees);
        StringBuilder formatHolder = new StringBuilder();

        formatHolder.append(format("%03d°", coors[0]))
                    .append(format("%02d'", coors[1]))
                    .append(format("%02d\"", coors[2]));
        return format("%-10s", formatHolder);
    }


    /**
     преобразует эклиптическую долготу в зодиакальную
     */
    public static String zodiacFormat(double position) {
        return "%s\t%s"
                .formatted(pointsZodium(position),
                        secondFormat(position % 30, true));
    }

    public static double findMedian(double positionA, double positionB) {
        double arc = getArc(positionA, positionB);
        double minorPosition =
                normalizeCoordinate(positionA + arc) == positionB ?
                positionA : positionB;

        double median = normalizeCoordinate(minorPosition + arc / 2);

        if (arc == CIRCLE / 2)
            median = -median;
        return median;
    }

    public static String zodiacDegree(double position) {

        return "%d°%s"
                .formatted(
                        (int) Math.ceil(position % 30),
                        pointsZodium(position));
    }

    /**
     * Превращает координату дуги из градусов в массив [градусы, минуты, секунды].
     * @param position дуга в градусах и дробных долях градуса.
     * @return массив из трёх целых величин: градусы, минуты и секунды.
     */
    public static int[] degreesToCoors(double position) {
        int[] coors = new int[3];
        int inSeconds = (int) round((normalizeCoordinate(position) * 3600));
        coors[0] = inSeconds / 3600;
        coors[1] = inSeconds % 3600 / 60;
        coors[2] = inSeconds % 60;
        return coors;
    }

    public static List<Integer> multipliersExplicate(int number) {
        if (number < 0) throw new IllegalArgumentException("функция работает с положительными числами");
        if (number == 0) return List.of(0);
        if (number == 1) return List.of(1);
        List<Integer> multipliers = new ArrayList<>();

        int divider = 2;
        while(number > 1) {
            if (divider > number / divider) {
                multipliers.add(number);
                break;
            }
//            System.out.println(number + " / " + divider);
            if (number % divider == 0) {
                multipliers.add(divider);
                number /= divider;
            } else {
                divider++;
            }
        }

        multipliers.sort(Comparator.reverseOrder());

        return multipliers;
    }


    /**
     * вспомогательный метод нахождения крата аспекта
     */
    public static int findMultiplier(int resonance, double arc, double orb) {
        double single = CIRCLE / resonance;
        int multiplier = 1;
        double orbHere = orb / resonance;
        while (multiplier < resonance / 2)
            if (abs(multiplier * single - arc) < orbHere) break;
            else multiplier++;
        return multiplier;
    }


    public static void main(String[] args) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 108; i++) {
            output.append("%3d → ".formatted(i));
            List<Integer> multi = multipliersExplicate(i);
            for (int m = 0; m < multi.size(); m++) {
                output.append(multi.get(m));
                if (m != multi.size() - 1)
                    output.append(" + ");
            }
            if (multi.size() > 1)
                output.append(" = ").append(multi.stream().mapToInt(Integer::intValue).sum());
            output.append("\n");
        }
        System.out.println(output);
    }

}
