package ru.swetophor.celestialmechanics;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static ru.swetophor.celestialmechanics.ZodiacSign.zodiumIcon;

public class CelestialMechanics {
    public static final double CIRCLE = 360.0;
    static double HALF_CIRCLE = 180.0;

    /**
     * вычисляет эклиптическую дугу между двумя точками на большом круге
     *
     * @param a первая координата точки.
     * @param b вторая координата точки.
     * @return наименьшую дугу между двумя указанными точками дуги.
     */
    public static double getArc(double a, double b) {
        double arc = abs(normalizeCoordinate(a) - normalizeCoordinate(b));
        return arc > HALF_CIRCLE ?
                CIRCLE - arc :
                arc;
    }

    /**
     * вычисляет эклиптическую дугу между астрами, переданными как объекты
     *
     * @param a первая астра.
     * @param b вторая астра.
     * @return наименьшую дугу между двумя указанными астрами.
     */
    public static double getArc(Astra a, Astra b) {
        return getArc(a.getZodiacPosition(), b.getZodiacPosition());
    }

    /**
     * приводит дугу к расстоянию меж ея концами
     */
    public static double normalizeArc(double a) {
        return getArc(normalizeCoordinate(a), 0);
    }

    /**
     * Приводит координату в диапазон от 0° до 359°59'59".
     *
     * @param p нормализуемая координата.
     * @return координату от 0° до 359°59'59", равную данной.
     */
    public static double normalizeCoordinate(double p) {
        p %= CIRCLE;
        return p < 0 ? p + CIRCLE : p;
    }

    /**
     * Находит среднюю координату (мидпойнт) для двух зодиакальных позиций.
     *
     * @param positionA первая позиция.
     * @param positionB вторая позиция.
     * @return среднюю координату между двумя заданными со стороны меньшей
     * дуги между ними. Если дуга равна ровно половине Круга, возвращается точка,
     * следующая через четверть после точки, переданной первой.
     */
    public static double findMedian(double positionA, double positionB) {
        double arc = getArc(positionA, positionB);
        double minorPosition =
                normalizeCoordinate(positionA + arc) == positionB ?
                        positionA : positionB;

        return normalizeCoordinate(minorPosition + arc / 2);
    }

    /**
     * Выдаёт зодиакальный градус указанной зодиакальной позиции в виде «градус°символ»
     *
     * @param position зодиакальная позиция.
     * @return строковое представление градуса и знака Зодиака.
     */
    public static String zodiacDegree(double position) {

        return "%d°%s"
                .formatted(
                        (int) Math.ceil(position % 30),
                        zodiumIcon(position));
    }

    /**
     * Превращает координату дуги из градусов в массив [градусы, минуты, секунды].
     *
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

    /**
     * Определяет, находится ли вторая координата в первой половине круга,
     * считая от первой координаты.
     * Например, если Луна находится в первой половине круга от Солнца,
     * она является растущей, иначе – убывающей.
     *
     * @param fromCoordinate координата, от которой считается.
     * @param coordinate     координата, для которой определяется фаза.
     * @return {@code true}, если вторая координата находится в соединении
     * с первой или менее, чем в 180° от неё по ходу движения Зодиака.
     * {@code false}, если вторая координата находится в 180° или более от
     * первой по ходу движения Зодиака.
     */
    public static boolean isAhead(double fromCoordinate, double coordinate) {
        double delta = coordinate - fromCoordinate;
        return delta >= 0 && delta < HALF_CIRCLE || delta < -HALF_CIRCLE;
    }

    /**
     * Определяет, находится ли вторая астра в первой половине круга,
     * считая от первой астры.
     * Например, если Луна находится в первой половине круга от Солнца,
     * она является растущей, иначе – убывающей.
     *
     * @param fromPlanet астра, от которой считается.
     * @param planet     астра, для которой определяется фаза.
     * @return {@code true}, если вторая астра находится в соединении
     * с первой или менее, чем в 180° от неё по ходу движения Зодиака.
     * {@code false}, если вторая астра находится в 180° или более от
     * первой по ходу движения Зодиака.
     */
    public static boolean isAhead(Astra fromPlanet, Astra planet) {
        return isAhead(fromPlanet.getZodiacPosition(), planet.getZodiacPosition());
    }
}
