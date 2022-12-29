package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.celestialmechanics.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
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
    private List<Aspect> aspects;     //

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
            IntStream.rangeClosed(1, ultimateHarmonic).forEach(h -> {
                double arcInHarmonic = normalizeArc(arc * h);
                if (arcInHarmonic < orb && isNewSimple(h))
                    aspects.add(new Aspect(h, arcInHarmonic, arc, orb));
            });
        }
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
                    findMultiplier(aNewNumber, arc, orb) == 1)
                continue;

            return false;
        }
        return true;
    }

    private String resoundsInfo() {
        StringBuilder sb = new StringBuilder();

        if (aspects.isEmpty()) {
            sb.append("Ни одного резонанса до %d при орбисе %s%n".formatted(ultimateHarmonic, orb));
        }
        for (Aspect aspect : getAspectsByStrength()) {
            sb.append(ResonanceDescription(aspect.numeric, aspect.multiplier));
            sb.append("Резонанс %d (x%d) - %s как %d (%.2f%%, %s)%n".formatted(
                    aspect.numeric,
                    aspect.multiplier,
                    aspect.strengthLevel(),
                    aspect.depth,
                    aspect.strength,
                    secondFormat(aspect.clearance, true)));
        }
        return sb.toString();
    }

    public String resonancesOutput() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case SELF -> sb.append("%n%s %s (%s)%n".formatted(astra_1,
                    whose_a1.getAstra(astra_1).getZodiacDegree(),
                    whose_a1.getName()));
            case CHART -> {
                sb.append("%n* Дуга между %c %s и %c %s (%s) = %s%n".formatted(
                        AstraEntity.findSymbolByName(astra_1), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_1)),
                        AstraEntity.findSymbolByName(astra_2), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_2)),
                        whose_a1.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport(aspects));
            }
            case SYNASTRY -> {
                sb.append("%n* Дуга между %c %s (%s) и %c %s (%s) = %s%n".formatted(
                        AstraEntity.findSymbolByName(astra_1), Mechanics.zodiacDegree(whose_a1.getAstraPosition(astra_1)),
                        whose_a1.getName(),
                        AstraEntity.findSymbolByName(astra_2), Mechanics.zodiacDegree(whose_a2.getAstraPosition(astra_2)),
                        whose_a2.getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport(aspects));
            }
        }
        return sb.toString();

    }

    private List<Aspect> getAspectsByStrength() {
        return aspects.stream()
                .sorted(Comparator.comparing(Aspect::getStrength).reversed())
                .collect(Collectors.toList());
    }


    public String resoundsReport(List<Aspect> resonances) {
        StringBuilder sb = new StringBuilder();

        if (resonances.isEmpty()) {
            sb.append("Ни одного резонанса до %d при орбисе %s%n".formatted(ultimateHarmonic, orb));
        }
        for (Aspect aspect : resonances) {
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


}
