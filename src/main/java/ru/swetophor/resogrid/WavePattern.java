package ru.swetophor.resogrid;

import ru.swetophor.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static ru.swetophor.Settings.getOrbDivider;
import static ru.swetophor.celestialmechanics.Mechanics.*;

public class WavePattern {
    public static void main(String[] args) {
        Rose.showPatternUpTo(13);
        System.out.println(reportOverlapping());
    }

    private static int edgeOfPatternHarmonics = Settings.getEdgeHarmonic();

    private static String reportOverlapping() {
        if (Rose.patterns.size() == 0) return "Расчёт ещё не выполнен!";
        StringBuilder message = new StringBuilder();
        int a = 0;
        int b = 0;
        while (a < Rose.patterns.size() - 1) {
            if (Rose.patterns.get(a).getEnd() > Rose.patterns.get(++a).getBeginning()) {
                message
                    .append(
                          format(
                            "Нахлёст %s между %d/%d (%s) и %d/%d (%s)%n",
                                    secondFormat(Rose.patterns.get(--a).getEnd() - Rose.patterns.get(++a).getBeginning(), true),
                                    Rose.patterns.get(--a).multiplier,
                                    Rose.patterns.get(a).harmonic,
                                    secondFormat(Rose.patterns.get(a).arc, true),
                                    Rose.patterns.get(++a).multiplier,
                                    Rose.patterns.get(a).harmonic,
                                    secondFormat(Rose.patterns.get(a).arc, true)
                          )
                    );
                b++;
            } else {
                a++;
            }
        }
        if (b == 0)
            message.append(format("Никаких пересечений до гармоники %d", edgeOfPatternHarmonics));
        else
            message.append(format("Всего найдено %d пересечений, считая до гармоники %d", b, edgeOfPatternHarmonics));

        message.append(" при орбисе %s (1/%d часть круга)%n".formatted(
                secondFormat(CIRCLE / getOrbDivider(), true),
                getOrbDivider()));
        return message.toString();
    }

    private static void showPatternUpTo(int harmonicsEdge) {
        edgeOfPatternHarmonics = harmonicsEdge;
        List<Aspect> patterns = new ArrayList <>();
        patterns.add(new Aspect(1, 1));

        for (int harmonic = 1; harmonic <= edgeOfPatternHarmonics; harmonic++)
            Rose.fillWavePattern(harmonic, patterns);

        patterns = orderedAspects(patterns);

        for (Aspect next : patterns) System.out.printf("%2d/%d\t%10s\t(%10s - %10s)%n",
                next.multiplier, next.harmonic,
                secondFormatForTable(next.arc, true),
                secondFormatForTable(next.getBeginning(), true),
                secondFormatForTable(next.getEnd(), true));
    }

    private static List<Aspect> orderedAspects(List<Aspect> patterns) {
        List<Aspect> orderedAspects = new ArrayList <>();
            while (orderedAspects.size() < patterns.size()) {
                int next = 0;
                while (orderedAspects.contains(patterns.get(next)))
                    next++;

                if (orderedAspects.size() < patterns.size() - 1) {
                    int following = next + 1;
                    while (orderedAspects.contains(patterns.get(following)))
                        following++;

                    while (following < patterns.size()) {
                        if (patterns.get(following).arc < patterns.get(next).arc)
                            next = following;
                        following++;

                        while (following < patterns.size() && orderedAspects.contains(patterns.get(following)))
                            following++;
                    }
                }
                orderedAspects.add(patterns.get(next));
            }
        return orderedAspects;
    }

    /**
     * Модель идеального аспекта, задаваемого скольки-то множителями какого-то делителя,
     * равного некоторой дуге и обладающего определённым орбом.
     */
    static class Aspect implements Comparable<Aspect> {
        /**
         * Гармоника, делитель аспекта.
         */
        int harmonic;
        /**
         * Множитель, крат аспекта.
         */
        int multiplier;
        /**
         * Дуга, соответствующая аспекту.
         */
        double arc;
        /**
         * Орб, надлежащий аспекту быть действительным.
         */
        double aspectOrbs;

        /**
         * Создать аспект как множитель делителей.
         * Дуга и орб высчитываются по заданному в настройках стандарту.
         * @param i гармоника, делитель.
         * @param k множитель, крат.
         */
        public Aspect(int i, int k) {
            this.harmonic = i;
            this.multiplier = k;
            this.arc = normalizeCoordinate(CIRCLE / harmonic * multiplier);
            this.aspectOrbs = CIRCLE / getOrbDivider() / harmonic;
        }
        /**
         * Начало действия аспекта.
         * @return  дугу, соответствующую нижней границе начала действия аспекта
         * (0, если начинается с соединения).
         */
        private double getBeginning() {
            double beginning = arc - aspectOrbs;
            return beginning >= 0 ? beginning : 0;
        }
        /**
         * Конец действия аспекта.
         * @return дугу, соответствующую верхней границе действия аспекта
         * (180, если превышает оппозицию).
         */
        private double getEnd() {
            double end = arc + aspectOrbs;
            return end <= CIRCLE/2 ? end : 180.;
        }
        @Override
        public String toString() {
            return "%s %d/%d (%s - %s)".formatted(
                        secondFormat(arc, false),
                        multiplier,
                        harmonic,
                        secondFormat(getBeginning(), false),
                        secondFormat(getEnd(), false)
                    );
        }

        /**
         * Сравнивает аспекты по дуге.
         * @param o the object to be compared. 
         * @return положительное значение, если дуга этого аспекта больше того,
         * с которым сравнивается; отрицательное, если этот меньше того; ноль если равны.
         */
        @Override
        public int compareTo(Aspect o) {
            return (int) (arc - o.arc);
        }
    }

    private static class Rose {
        static List<Aspect> patterns;
        public static void showPatternUpTo(int harmonics) {
            createPattern(harmonics);
            StringBuilder sb = new StringBuilder();
            patterns.forEach(next -> sb.append(
                    "%2d/%d\t%10s\t(%10s - %10s)%n".formatted(
                    next.multiplier,
                    next.harmonic,
                    secondFormatForTable(next.arc, true),
                    secondFormatForTable(next.getBeginning(), true),
                    secondFormatForTable(next.getEnd(), true)))
            );
            System.out.println(sb);
        }

        private static void createPattern(int harmonics) {
//            patterns = new ArrayList <>();
            patterns.add(new Aspect(1, 1));            // до конца не удалось понять, почему нужна эта строка

            IntStream.rangeClosed(1, harmonics).forEach(
                    harmonic -> fillWavePattern(harmonic, patterns)
            );

            patterns = orderedAspects(patterns);
        }

        private static void fillWavePattern(int harmonic, List<Aspect> patterns) {
            double single = normalizeCoordinate(CIRCLE / harmonic);
            multipliersIteration:
            for (int multiplier = 1; multiplier <= harmonic/2; multiplier++) {

                for (Aspect alreadyWritten : patterns)
                    if (single * multiplier == alreadyWritten.arc)
                        continue multipliersIteration;

                patterns.add(new Aspect(harmonic, multiplier));
            }
        }

    }
}
