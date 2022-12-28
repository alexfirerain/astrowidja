package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.Interpreter;
import ru.swetophor.celestialmechanics.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static ru.swetophor.celestialmechanics.Mechanics.*;
import static ru.swetophor.Interpreter.ResonanceDescription;
import static ru.swetophor.resogrid.ResonanceType.*;

/**
 * Структурированный массив, описывающий
 * взаимодействие (резонансы) между некоторыми двумя точками
 */
@Setter
@Getter
public class Resonance {
    /**
     * Ссылка на карту, в которой находится первая астра.
     */
    private final Chart whose_a1;
    /**
     * Ссылка на карту, в которой находится вторая астра.
     */
    private Chart whose_a2;
    /**
     * Тип резонанса:
     */
    private final ResonanceType type;
    /**
     * Название первой астры.
     */
    private String astra_1;
    /**
     * Название второй астры.
     */
    private String astra_2;
    /**
     * Угловое расстояние меж точками, т.е. дуга, резонансы которой считаются.
     */
    private double arc;
    /**
     * Принятый в карте орбис, т.е. допуск, с которым аспект считается действующим.
     */
    private double orb;
    /**
     * Наибольший проверяемый целочисленный резонанс
     */
    private int ultimateHarmonic;
    /**
     * найденные в пределах орбиса резонансы по росту гармоники
     */
    private ArrayList<Aspect> aspects;     //

    /**
     * один из действующих для данной дуги резонансов
     */
    class Aspect {

        /**
        гармоника
         */
        int numeric;              //

        /**
        дальность
         */
        int multiplier;               //

        /**
        разность дуги резонанса с чистым аспектом вида дальность/гармоника
         */
        double clearance;           //

        /**
        разность дуги резонанса с чистым аспектом % от наиточнейшего
         */
        double strength;            //

        /**
        точность аспекта через количество последующих гармоник, через кои проходит
         */
        int depth;

        /**
         * Выводит характеристику, насколько точен резонанс.
         * @return строковое представление ранга точности.
         */
        String strengthLevel() {
            if (depth <= 1) return "- приблизительный ";
            else if (depth == 2) return "- уверенный ";
            else if (depth <= 5) return "- глубокий ";
            else if (depth <= 12) return "- точный ";
            else if (depth <= 24) return "- глубоко точный ";
            else return "- крайне точный ";
        }

        String strengthRating() {
            if (depth <= 1) return "★";
            else if (depth == 2) return "★★";
            else if (depth <= 5) return "★★★";
            else if (depth <= 12) return "★★★★";
            else if (depth <= 24) return "★★★★★";
            else return "✰✰✰✰✰";
        }

        Aspect(int numeric, double clearance, double fromArc) {
            this.numeric = numeric;
            this.multiplier = findMultiplier(numeric, fromArc);
            this.clearance = clearance;
            this.strength = ((orb - clearance) / orb) * 100;
            this.depth = (int) floor(orb / clearance);
        }

        public double getStrength() {
            return strength;
        }
    }

    /**
     * Получение массива аспектов для дуги между двумя астрами (конструктор)
     * @param a первая астра резонанса.
     * @param b вторая астра резонанса.
     * @param orb первичный орбис резонанса.
     * @param ultimateHarmonic  до какой гармоники продолжать анализ.
     */
    Resonance(Astra a, Astra b, double orb, int ultimateHarmonic) {
        astra_1 = a.getName();
        whose_a1 = a.getHeaven();
        if (a.equals(b)) {
            type = SELF;
        } else {
            astra_2 = b.getName();
            whose_a2 = b.getHeaven();
            type = whose_a1.getID() == whose_a2.getID() ?
                    CHART : SYNASTRY;
            astra_1 = a.getName();
            astra_2 = b.getName();
            arc = Mechanics.getArc(a, b);
            this.orb = orb;
            this.ultimateHarmonic = ultimateHarmonic;

            aspects = new ArrayList<>();
            for (int h = 1; h <= ultimateHarmonic; h++) {
                double arcInHarmonic = normalizeArc(arc * h);
                if (arcInHarmonic < orb && isNewSimple(h)) {
                    aspects.add(new Aspect(h, arcInHarmonic, arc));
                }
            }

//            IntStream.range(1, ultimateHarmonic + 1).forEach(h -> {
//                double arcInHarmonic = normalizeArc(arc * h);
//                if (arcInHarmonic < orb && isNotRepeating(h))
//                    resounds.add(new Resound(h, arcInHarmonic, arc));
//            });
        }
    }

    /**
     * вспомогательный метод нахождения крата аспекта
     */
    private int findMultiplier(int resonance, double arc) {
        double single = CIRCLE / resonance;
        int multiplier = 1;
        double orbHere = orb / resonance;
        while (multiplier < resonance / 2)
            if (abs(multiplier * single - arc) < orbHere) {
                break;
            } else {
                multiplier++;
            }
        return multiplier;
    }

    /**
     * Вспомогательный метод отсечения кратных гармоник при заполнении списка отзвуков.
     * @param aNewNumber число, которое проверяется на кратность уже найденным отзвукам.
     * @return истинно, если проверяемое число не кратно никакому из уже найденных (кроме 1),
     * а также не является точным соединением, проходящим до данной гармоники.
     */
    private boolean isNewSimple(int aNewNumber) {
        boolean isConjunction = false;

        for (Aspect next : aspects) {
            int aFoundHarmonic = next.numeric;

            if (aFoundHarmonic == 1)
                isConjunction = true;

            if (aNewNumber % aFoundHarmonic != 0)
                continue;

            if (isConjunction &&
                    arc > orb / aNewNumber &&
                    findMultiplier(aNewNumber, arc) == 1)
                continue;

            return false;
        }
        return true;
    }

    private String resoundsReport() {
        StringBuilder sb = new StringBuilder();

        if (aspects.size() == 0) {
            sb.append("Ни одного резонанса до %d при орбисе %s%n".formatted(ultimateHarmonic, orb));
        }
        for (Aspect aspect : getAspectsByStrength()) {
            sb.append(ResonanceDescription(aspect.numeric, aspect.multiplier));
            sb.append("Резонанс %d (x%d) %s (%d) (%.2f%%, %s)%n".formatted(
                    aspect.numeric,
                    aspect.multiplier,
                    aspect.strengthLevel(),
                    aspect.depth,
                    aspect.strength,
                    secondFormat(aspect.clearance, true)));
        }
        return sb.toString();
    }

    private String resoundsReport2() {
        StringBuilder sb = new StringBuilder();

        if (aspects.size() == 0) {
            sb.append("Ни одного резонанса до %d при орбисе %s%n".formatted(ultimateHarmonic, orb));
        }
        for (Aspect aspect : getAspectsByStrength()) {
            sb.append(ResonanceDescription(aspect.numeric, aspect.multiplier));
            sb.append("Резонанс %d/%d %s (%.0f%%) --- %.2f %n".formatted(
                    aspect.multiplier,
                    aspect.numeric,
                    aspect.strengthRating(),
                    aspect.strength,
                    aspect.strength / Math.pow(Math.log(aspect.numeric + 1.0), 0.5)));
        }
        return sb.toString();
    }

    public String resonancesOutput() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case SELF -> sb.append("%n%s (%s)%n".formatted(astra_1, whose_a1.getName()));
            case CHART -> {
                sb.append("%n* Дуга между %c %s и %c %s (%s) = %s%n".formatted(
                        AstraEntity.findSymbolByName(astra_1), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_1)),
                        AstraEntity.findSymbolByName(astra_2), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_2)),
                        whose_a1.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport2());
            }
            case SYNASTRY -> {
                sb.append("%n* Дуга между %c %s (%s) и %c %s (%s) = %s%n".formatted(
                        AstraEntity.findSymbolByName(astra_1), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_1)),
                        whose_a1.getName(),
                        AstraEntity.findSymbolByName(astra_2), Mechanics.zodiacDegree(whose_a2.getAstraPosition(astra_2)),
                        whose_a2.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport2());
            }
        }
        return sb.toString();

    }

    private List<Aspect> getAspectsByStrength() {
        return aspects.stream()
                .sorted(Comparator.comparing(Aspect::getStrength).reversed())
                .collect(Collectors.toList());
    }


    public static void main(String[] args) {
        IntStream.range(0, 30)
                .forEach(i -> System.out.println(Interpreter.multipliersExplicate(i)));
    }

}
