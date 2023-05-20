package ru.swetophor.harmonix;

import ru.swetophor.celestialmechanics.CelestialMechanics;
import ru.swetophor.celestialmechanics.Mechanics;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.swetophor.celestialmechanics.CelestialMechanics.normalizeArc;

public class Harmonics extends Number implements Comparable<Harmonics> {
    /**
     * Номинал: какому числу кратность задаёт гармонику.
     */
    private final int number;
    /**
     * Простые множители (не считая Единицы), в произведении дающие это число.
     */
    private final Integer[] multipliers;


    public Harmonics(int number) {
        this.number = number;
        multipliers = Mechanics.multipliersExplicate(number).toArray(Integer[]::new);
    }

    public static void buildHeavens(int uptoNumber) {
        StringBuilder report = new StringBuilder();
        int heaven = 0;
        for (int i = 1; i <= uptoNumber; i++) {
            StringBuilder heavenBuilder = new StringBuilder();
            List<Integer> multi = Mechanics.multipliersExplicate(i);
            int n = 0;
            int heavenSum = 0;
            if (multi.size() == 1) {
                heavenBuilder.append("\t%s Σ = %d (сложность %d)%n"
                        .formatted(Mechanics.formatMultipliers(multi), Mechanics.multiSum(i), multi.size()));
                n++;
                heavenSum += Mechanics.multiSum(i);

                while (Mechanics.multipliersExplicate(i + 1).size() > 1) {
                    List<Integer> nextMulti = Mechanics.multipliersExplicate(++i);
                    heavenBuilder.append("\t%s Σ = %d (сложность %d)%n"
                            .formatted(Mechanics.formatMultipliers(nextMulti), Mechanics.multiSum(i), nextMulti.size()));
                    n++;
                    heavenSum += Mechanics.multiSum(i);
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

        for (Harmonics h : generateUpTo(uptoNumber))
            if (h.isSimple()) {
                if (!heavenBuilder.isEmpty())
                    report.append("Небо №%d (высота = %.2f)%n"
                                    .formatted(heaven++, (double) heavenSum / (double) n))
                            .append(heavenBuilder);
                heavenBuilder = new StringBuilder("\t%s Σ = %d (сложность %d)%n"
                        .formatted(
                                Mechanics.formatMultipliers(h.getMultipliers()),
                                h.multipliersSum(),
                                h.complexity()));
                n = 1;
                heavenSum = h.multipliersSum();
            } else {
                heavenBuilder.append("\t%s Σ = %d (сложность %d)%n"
                        .formatted(
                                Mechanics.formatMultipliers(h.getMultipliers()),
                                h.multipliersSum(),
                                h.complexity()));
                n++;
                heavenSum += h.multipliersSum();
            }

        System.out.println(report);
    }

    public int getNumber() {
        return number;
    }


    /**
     * Отдаёт простые множители, дающие в произведении это число.
     * @return  массив простых чисел, являющихся множителями данного числа
     * и не являющихся единицей. Они сортированы от большего к меньшему.
     * Если номинал гармоники является простым числом, результат содержит только само это число.
     */
    public Integer[] getMultipliers() {
        return multipliers;
    }

    /**
     * Сообщает, является ли гармоника простым числом.
     * @return {@code истинно}, если массив простых неединичных множителей содержит только одно число,
     * т.е. число делится без остатка только на 1 и на себя.
     * {@code Ложно}, если номинал гармоники не является простым числом.
     */
    public boolean isSimple() {
        return complexity() == 1;
    }

    /**
     * Рапортует сложность числа, т.е. количество множителей, на которые оно разлагается.
     * @return  количество простых множителей, дающих эту гармонику.
     */
    public int complexity() {
        return multipliers.length;
    }

    /**
     * Создаёт массив гармоник от 1-ой до указанной включительно.
     * @param ultimateHarmonic предельная гармоника, которая будет построена.
     * @return массив последовательных гармоник от 1-ой до указанной включительно.
     */
    public static Harmonics[] generateUpTo(int ultimateHarmonic) {
        Harmonics[] row = new Harmonics[ultimateHarmonic];
        IntStream.rangeClosed(1, ultimateHarmonic)
                .forEach(i -> row[i - 1] = new Harmonics(i));
        return row;
    }

    /**
     * Генерирует стрем гармоник с 1-ой по указанную.
     * @param ultimateHarmonic последняя гармоника, на которой стрем остановится.
     * @return стрем гармоник с 1-ой по указанную.
     */
    public static Stream<Harmonics> streamUpTo(int ultimateHarmonic) {
        return IntStream.rangeClosed(1, ultimateHarmonic)
                .mapToObj(Harmonics::new);
    }

    /**
     * Возвращает длину дуги в градусах, соответствующую
     * единичной кратности Круга этому числу, т.е.длину "зодиака" в гармонике.
     * @return  частное от деления 360 на данное число. Ноль для первой гармоники.
     */
    public double getSinglePureArc() {
        return normalizeArc(CelestialMechanics.CIRCLE / doubleValue());
    }

    /**
     * @return сумму множителей этого числа (гармонический корень)
     */
    public int multipliersSum() {
        return Arrays.stream(multipliers)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * @return номинал гармоники.
     */
    @Override
    public int intValue() {
        return number;
    }

    /**
     * @return номинал как long.
     */
    @Override
    public long longValue() {
        return number;
    }

    /**
     * @return номинал как float.
     */
    @Override
    public float floatValue() {
        return (float) number;
    }

    /**
     * @return номинал как double.
     */
    @Override
    public double doubleValue() {
        return number;
    }

    /**
     * @param harmonics число, с которым сравниваем.
     * @return положительное число, если эта гармоника выше указанной,
     * ноль, если равно указанному, отрицательное, если меньше указанного.
     */
    @Override
    public int compareTo(Harmonics harmonics) {
        return getNumber() - harmonics.getNumber();
    }

    public Harmonics getNext() {
        return new Harmonics(number + 1);
    }

}
