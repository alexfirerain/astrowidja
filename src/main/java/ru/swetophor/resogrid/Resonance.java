package ru.swetophor.resogrid;

import lombok.Getter;
import lombok.Setter;
import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.Mechanics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.Interpreter.ResonanceDescription;
import static ru.swetophor.celestialmechanics.Mechanics.*;
import static ru.swetophor.resogrid.ResonanceType.*;

/**
 * Структурированный массив, описывающий
 * взаимодействие (резонансы) между некоторыми двумя точками
 */
@Setter
@Getter
public class Resonance {


    /**
     * Тип резонанса:
     */
    private final ResonanceType type;
    /**
     * Название первой астры.
     */
    private Astra astra_1;
    /**
     * Название второй астры.
     */
    private Astra astra_2;
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
        astra_1 = a;
        if (a.equals(b)) {
            type = SELF;
        } else {
            astra_2 = b;
            type = a.getHeaven().getID() == b.getHeaven().getID() ?
                    CHART : SYNASTRY;
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
     * Вспомогательный метод отсечения кратных гармоник при заполнении списка отзвуков.
     * @param aNewNumber число, которое проверяется на кратность уже найденным отзвукам.
     * @return {@code истинно}, если проверяемое число не кратно никакому из уже найденных (кроме 1),
     * а также не является точным соединением, проходящим до данной гармоники. Следовательно,
     * эту гармонику надо брать в набор. Если же {@code ложно}, брать её в набор не нужно.
     */
    private boolean isNewSimple(int aNewNumber) {
        boolean isConjunction = false;

        for (Aspect next : aspects) {
            int aPreviousHarmonic = next.numeric;

            if (aPreviousHarmonic == 1)
                isConjunction = true;

            if (aNewNumber % aPreviousHarmonic != 0)
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
            case SELF -> sb.append("%n%c %s (%s)%n".formatted(
                    astra_1.getSymbol(), astra_1.getZodiacDegree(),
                    astra_1.getHeaven().getName()));
            case CHART -> {
                sb.append("%n* Дуга между %c %s и %c %s (%s) = %s%n".formatted(
                        astra_1.getSymbol(), astra_1.getZodiacDegree(),
                        astra_2.getSymbol(), astra_2.getZodiacDegree(),
                        astra_1.getHeaven().getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
            case SYNASTRY -> {
                sb.append("%n* Дуга между %c %s (%s) и %c %s (%s) = %s%n".formatted(
                        astra_1.getSymbol(), astra_1.getZodiacDegree(),
                        astra_1.getHeaven().getName(),
                        astra_2.getSymbol(), astra_2.getZodiacDegree(),
                        astra_2.getHeaven().getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
        }
        return sb.toString();

    }

    private List<Aspect> getAspectsByStrength() {
        return aspects.stream()
                .sorted(Comparator.comparing(Aspect::getStrength).reversed())
                .collect(Collectors.toList());
    }


    public String resoundsReport() {
        StringBuilder sb = new StringBuilder();

        if (aspects.isEmpty()) {
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

}
