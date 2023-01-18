package ru.swetophor.celestialmechanics;

import ru.swetophor.resogrid.Harmonics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.String.format;
import static ru.swetophor.celestialmechanics.ZodiacSign.zodiumIcon;

/**
 * Инструментальный класс для решения
 * задач по пространственному расположению
 */
public class Mechanics {

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
        int[] coors = CelestialMechanics.degreesToCoors(inDegrees);
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
        int[] coors = CelestialMechanics.degreesToCoors(inDegrees);
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
    public static String secondFormatForTable(double inDegrees, boolean withoutExtraZeros) {
        int[] coors = CelestialMechanics.degreesToCoors(inDegrees);
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
    public static String secondFormatForTable(double inDegrees) {
        int[] coors = CelestialMechanics.degreesToCoors(inDegrees);
        StringBuilder formatHolder = new StringBuilder();

        formatHolder.append(format("%03d°", coors[0]))
                    .append(format("%02d'", coors[1]))
                    .append(format("%02d\"", coors[2]));
        return format("%-10s", formatHolder);
    }


    /**
     *  преобразует эклиптическую долготу в зодиакальную
     * @param position эклиптическая долгота.
     * @return  строку, представляющую зодиакальную координату (знак + секундФормат без лишних нолей).
     */
    public static String zodiacFormat(double position) {
        return "%c\t%s"
                .formatted(zodiumIcon(position),
                        secondFormat(position % 30, true));
    }

    /**
     * Выдаёт список множителей данного числа (не считая единицы, естественно).
     * @param number неотрицательное число, разлагаемое на множители.
     * @return  список множителей, дающих исходное число, от большего к меньшему.
     */
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
     * @param resonance какой гармоники анализируется дуга.
     * @param arc   анализируемая дуга.
     * @param orb   первичный орбис, используемый при расчёте аспектов.
     * @return  множитель аспекта заданной гармоники для заданной дуги.
     */
    public static int findMultiplier(int resonance, double arc, double orb) {
        double single = CelestialMechanics.CIRCLE / resonance;
        int multiplier = 1;
        double orbHere = orb / resonance;
        while (multiplier < resonance / 2)
            if (abs(multiplier * single - arc) < orbHere) break;
            else multiplier++;
        return multiplier;
    }

    public static int multiSum(int number) {
        return multipliersExplicate(number).stream()
                .mapToInt(Integer::intValue).sum();
    }

    public static void displayMultipliers(int upto) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i <= upto; i++) {
            output.append("%3d → ".formatted(i));
            List<Integer> multi = multipliersExplicate(i);
            for (int m = 0; m < multi.size(); m++) {
                output.append(multi.get(m));
                if (m < multi.size() - 1)
                    output.append(" + ");
            }
            if (multi.size() > 1)
                output.append(" = ").append(multiSum(i));
            output.append("\n");
        }
        System.out.println(output);
    }

    public static void buildHeavens(int uptoNumber) {
        StringBuilder report = new StringBuilder();
        int heaven = 0;
        for (int i = 1; i <= uptoNumber; i++) {
            StringBuilder heavenBuilder = new StringBuilder();
            List<Integer> multi = multipliersExplicate(i);
            int n = 0;
            int heavenSum = 0;
            if (multi.size() == 1) {
                heavenBuilder.append("\t%s Σ = %d (сложность %d)%n"
                        .formatted(formatMultipliers(multi), multiSum(i), multi.size()));
                n++;
                heavenSum += multiSum(i);

                while (multipliersExplicate(i + 1).size() > 1) {
                    List<Integer> nextMulti = multipliersExplicate(++i);
                    heavenBuilder.append("\t%s Σ = %d (сложность %d)%n"
                            .formatted(formatMultipliers(nextMulti), multiSum(i), nextMulti.size()));
                    n++;
                    heavenSum += multiSum(i);
                }
                report.append("Небо №%d (высота = %.2f)%n"
                        .formatted(heaven++, (double) heavenSum / (double) n))
                      .append(heavenBuilder);
            }
        }
        System.out.println(report);
    }

    public static void buildHeavensWithHarmonics(int uptoNumber) {
        StringBuilder report = new StringBuilder();
        int heaven = 0;
        StringBuilder heavenBuilder = new StringBuilder();
        int n = 0;
        int heavenSum = 0;

        for (Harmonics h : Harmonics.generateUpTo(uptoNumber))
            if (h.isSimple()) {
                if (!heavenBuilder.isEmpty())
                    report.append("Небо №%d (высота = %.2f)%n"
                                    .formatted(heaven++, (double) heavenSum / (double) n))
                            .append(heavenBuilder);
                heavenBuilder = new StringBuilder("\t%s Σ = %d (сложность %d)%n"
                        .formatted(
                                formatMultipliers(h.getMultipliers()),
                                h.multipliersSum(),
                                h.complexity()));
                n = 1;
                heavenSum = h.multipliersSum();
            } else {
                heavenBuilder.append("\t%s Σ = %d (сложность %d)%n"
                        .formatted(
                                formatMultipliers(h.getMultipliers()),
                                h.multipliersSum(),
                                h.complexity()));
                n++;
                heavenSum += h.multipliersSum();
            }

        System.out.println(report);
    }


    public static void main(String[] args) {
//        displayMultipliers(108);
//        buildHeavens(108);
        buildHeavensWithHarmonics(144);
    }

    /**
     * Возвращает форматированную строку с переданными числами через "х",
     * вся группа заключена в <>.
     *
     * @param multipliers числа (множители, которые хотим форматировать).
     * @return форматированное представление множителей числа, переданных в аргумент как список.
     */
    public static String formatMultipliers(List<Integer> multipliers) {
        StringBuilder answer = new StringBuilder("<");
        if (!multipliers.isEmpty())
            answer.append(multipliers.get(0));
        for (int i = 1; i < multipliers.size(); i++)
            answer.append("x")
                    .append(multipliers.get(i));
        answer.append(">");
        return answer.toString();
    }

    /**
     * Возвращает форматированную строку с переданными числами через "х",
     * вся группа заключена в <>.
     *
     * @param multipliers числа (множители, которые хотим форматировать).
     * @return форматированное представление множителей числа, переданных в аргумент как массив.
     */
    public static String formatMultipliers(Integer[] multipliers) {
        StringBuilder answer = new StringBuilder("<");
        if (multipliers.length > 0)
            answer.append(multipliers[0]);
        IntStream.range(1, multipliers.length)
                .forEach(i -> answer.append("x").append(multipliers[i]));
        answer.append(">");
        return answer.toString();
    }


    /**
     * Скажет, является ли данная гармоника кратной некоторой другой гармонике.
     * @param harmonic  число резонанса, которое проверяем на кратность.
     * @param numeric   число резонанса, на кратность с которым проверяется.
     * @return  {@code true}, если один из простых множителей первого аргумента совпадает
     * со вторым аргументом.
     */
    public static boolean isMultiplied(int harmonic, int numeric) {
        return multipliersExplicate(harmonic)
                .contains(numeric);
    }

    public static boolean isMultiple(int number, int multiplier) {
        return number % multiplier == 0;
    }
}
