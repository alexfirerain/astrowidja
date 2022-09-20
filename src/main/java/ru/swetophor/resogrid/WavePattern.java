package ru.swetophor.resogrid;

import ru.swetophor.Settings;
import java.util.ArrayList;
import static java.lang.String.format;
import static ru.swetophor.Settings.getOrbisDivider;
import static ru.swetophor.celestialmechanics.Mechanics.*;

public class WavePattern {
    public static void main(String[] args) {
        Rose.showPatternUpTo(23);
        System.out.println(reportOverlapping());
    }

    private static int edgeOfPatternHarmonics = Settings.getEdgeHarmonic();

    private static String reportOverlapping() {
        if (Rose.patterns.size() == 0) return "Расчёт ещё не выполнен!";
        StringBuilder message = new StringBuilder();
        int э = 0;
        int п = 0;
        while (э < Rose.patterns.size() - 1) {
            if (Rose.patterns.get(э).getEnd() > Rose.patterns.get(++э).getBeginning()) {
                message.append(format("Нахлёст %s между %d/%d (%s) и %d/%d (%s)%n",
                        secondFormat(Rose.patterns.get(--э).getEnd() - Rose.patterns.get(++э).getBeginning(), true),
                        Rose.patterns.get(--э).multiplier, Rose.patterns.get(э).harmonic, secondFormat(Rose.patterns.get(э).arc, true),
                        Rose.patterns.get(++э).multiplier, Rose.patterns.get(э).harmonic, secondFormat(Rose.patterns.get(э).arc, true)));
                п++;
            } else {
                э++;
            }
        }
        if (п == 0)
            message.append(format("Никаких пересечений до гармоники %d", edgeOfPatternHarmonics));
        else message.append(format("Всего найдено %d пересечений, считая до гармоники %d", п, edgeOfPatternHarmonics));

        message.append(format(" при орбисе %s (1/%d часть круга)%n",
                secondFormat(CIRCLE / getOrbisDivider(), true),
                getOrbisDivider()));
        return message.toString();
    }

    private static void showPatternUpTo(int harmonicsEdge) {
        edgeOfPatternHarmonics = harmonicsEdge;
        ArrayList<Aspect> patterns = new ArrayList <>();
        double single;
        int harmonic = 1;
        patterns.add(new Aspect(1, 1));

        while (harmonic <= edgeOfPatternHarmonics) {
            single = normalizeCoordinate(CIRCLE / harmonic);
            multipliersIteration:
            for (int multiplier = 1; multiplier <= harmonic/2; multiplier++) {
                for (Aspect alreadyWritten : patterns)
                    if (single * multiplier == alreadyWritten.arc) continue multipliersIteration;
                patterns.add(new Aspect(harmonic, multiplier));
            }
            harmonic++;
        }

        patterns = orderedAspects(patterns);
        for (Aspect next : patterns) System.out.printf("%2d/%d\t%10s\t(%10s - %10s)%n",
                next.multiplier, next.harmonic,
                secondFormatTablewise(next.arc, true),
                secondFormatTablewise(next.getBeginning(), true),
                secondFormatTablewise(next.getEnd(), true));
    }

    private static ArrayList<Aspect> orderedAspects(ArrayList<Aspect> patterns) {
        ArrayList<Aspect> orderedAspects = new ArrayList <>();
            while (orderedAspects.size() < patterns.size()) {
                int next = 0;
                while (orderedAspects.contains(patterns.get(next))) next++;
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

    static class Aspect {
        int harmonic;
        int multiplier;
        double arc;
        double aspectOrbis;
        public Aspect(int i, int k) {
            this.harmonic = i;
            this.multiplier = k;
            this.arc = normalizeCoordinate(CIRCLE / harmonic * multiplier);
            this.aspectOrbis = CIRCLE / getOrbisDivider() / harmonic;
        }
        private double getBeginning() {
            if (arc - aspectOrbis >= 0) return arc - aspectOrbis;
            else return 0;
        }

        private double getEnd() {
            if (arc + aspectOrbis <= CIRCLE/2) return arc + aspectOrbis;
            else return 180;
        }
        @Override
        public String toString() {
            return secondFormat(arc, false) + " " + multiplier + "/" + harmonic
                    + " (" + secondFormat(getBeginning(), false) + " - " + getEnd() + ")";
        }
    }

    private static class Rose {
        static ArrayList<Aspect> patterns;
        public static void showPatternUpTo(int harmonics) {
            createPattern(harmonics);
            patterns.forEach(next -> System.out.printf("%2d/%d\t%10s\t(%10s - %10s)%n",
                    next.multiplier, next.harmonic,
                    secondFormatTablewise(next.arc, true),
                    secondFormatTablewise(next.getBeginning(), true),
                    secondFormatTablewise(next.getEnd(), true)));
        }

        private static void createPattern(int harmonics) {
            patterns = new ArrayList <>();
            double single;
            int harmonic = 1;
            patterns.add(new Aspect(1, 1));            // до конца не удалось понять, почему нужна эта строка
            while (harmonic <= harmonics) {
                single = normalizeCoordinate(CIRCLE / harmonic);
                multipliersIteration:
                for (int multiplier = 1; multiplier <= harmonic/2; multiplier++) {

                    for (Aspect alreadyWritten : patterns)
                        if (single * multiplier == alreadyWritten.arc)
                            continue multipliersIteration;

                    patterns.add(new Aspect(harmonic, multiplier));
                }
                harmonic++;
            }
            patterns = orderedAspects(patterns);
        }

    }
}
