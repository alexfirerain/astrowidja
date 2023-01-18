package ru.swetophor.resogrid;

import lombok.Setter;
import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.CelestialMechanics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.Mechanics.*;
import static ru.swetophor.mainframe.Interpreter.ResonanceDescription;
import static ru.swetophor.resogrid.ResonanceType.*;

/**
 * Гармонический анализ взаимодействия некоторых двух астр.
 * Содержит ссылки на две точки (астры) и {@link #aspects список} рассчитанных
 * для этой {@link #arc дуги} {@link Aspect Аспектов}.
 */
@Setter
public class Resonance {
    /**
     * Тип резонанса:
     */
    private final ResonanceType type;
    /**
     * Первая астра.
     */
    private Astra astra_1;
    /**
     * Вторая астра.
     */
    private Astra astra_2;
    /**
     * Угловое расстояние меж точками, т.е. дуга, резонансы которой считаются.
     */
    private double arc;
    /**
     * Максимальный орбис для соединения, т.е. базовый допуск, с которым при расчётах считается,
     * что резонанс возник. Для каждой гармоники базовый орбис пропорционально уменьшается.
     */
    private double orb;
    /**
     * Наибольший проверяемый целочисленный резонанс.
     */
    private int ultimateHarmonic;
    /**
     * Найденные в пределах орбиса аспекты по росту гармоники.
     */
    private List<Aspect> aspects = new ArrayList<>();

    /**
     * Получение массива аспектов для дуги между двумя астрами (конструктор)
     * @param a первая астра резонанса.
     * @param b вторая астра резонанса.
     * @param orb первичный орбис резонанса.
     * @param ultimateHarmonic  до какой гармоники продолжать анализ.
     */
    Resonance(Astra a, Astra b, double orb, int ultimateHarmonic) {
        type = a == b ?
                IN_SELF :
                a.getHeaven() == b.getHeaven() ?
                        IN_MOMENT :
                        INTER_MOMENTS;
        astra_1 = a;
        astra_2 = b;
        arc = CelestialMechanics.getArc(a, b);
        this.orb = orb;
        this.ultimateHarmonic = ultimateHarmonic;

        if (type == IN_SELF)
            return;

        IntStream.rangeClosed(1, ultimateHarmonic).forEach(h -> {
            double arcInHarmonic = CelestialMechanics.normalizeArc(arc * h);
            if (arcInHarmonic < orb && isNewSimple(h))
                    aspects.add(new Aspect(h, arcInHarmonic, arc, orb));
        });
    }

    /**
     * Вспомогательный метод отсечения кратных гармоник при заполнении списка аспектов.
     * @param aNewNumber число, которое проверяется на кратность уже найденным аспектам.
     * @return {@code истинно}, если проверяемое число не кратно никакому из {@link #aspects уже найденных} (кроме 1),
     * а также не является точным соединением, проходящим до данной гармоники. Следовательно,
     * эту гармонику надо брать в {@link #aspects набор}. Если же {@code ложно}, брать её в набор не нужно.
     */
    private boolean isNewSimple(int aNewNumber) {
        boolean isConjunction = false;

        for (Aspect next : aspects) {
            int aPreviousHarmonic = next.getNumeric();

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
            sb.append(ResonanceDescription(aspect.getNumeric(), aspect.getMultiplicity()));
            sb.append("Резонанс %d (x%d) - %s как %d (%.2f%%, %s)%n".formatted(
                    aspect.getNumeric(),
                    aspect.getMultiplicity(),
                    aspect.strengthLevel(),
                    aspect.getDepth(),
                    aspect.getStrength(),
                    secondFormat(aspect.getClearance(), true)));
        }
        return sb.toString();
    }

    public String resonancesOutput() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case IN_SELF -> sb.append("%n%c %s (%s)%n".formatted(
                    astra_1.getSymbol(), astra_1.getZodiacDegree(),
                    astra_1.getHeaven().getName()));
            case IN_MOMENT -> {
                sb.append("%n* Дуга между %c %s и %c %s (%s) = %s%n".formatted(
                        astra_1.getSymbol(), astra_1.getZodiacDegree(),
                        astra_2.getSymbol(), astra_2.getZodiacDegree(),
                        astra_1.getHeaven().getName(),
                        secondFormat(arc, true)));
                sb.append(resoundsReport());
            }
            case INTER_MOMENTS -> {
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
        getAspectsByStrength().forEach(aspect -> {
            sb.append(ResonanceDescription(aspect.getNumeric(), aspect.getMultiplicity()));
            sb.append("Резонанс %d/%d %s (%.0f%%) --- %.2f %n".formatted(
                    aspect.getMultiplicity(),
                    aspect.getNumeric(),
                    aspect.strengthRating(),
                    aspect.getStrength(),
                    aspect.getStrength() / Math.pow(Math.log(aspect.getNumeric() + 1.0), 0.5)));
        });
        return sb.toString();
    }

    public boolean hasResonanceElement(int harmonic) {
        return aspects.stream()
                .anyMatch(a -> a.getMultipliers().contains(harmonic));
    }

    public boolean hasGivenHarmonic(int harmonic) {
        return aspects.stream()
                .anyMatch(a -> a.getNumeric() == harmonic);
    }

    public boolean hasHarmonicPattern(int harmonic) {
        return aspects.stream()
                .anyMatch(a -> a.hasResonance(harmonic));
    }


    public ResonanceType getType() {
        return this.type;
    }

    public Astra getAstra_1() {
        return astra_1;
    }

    public Astra getAstra_2() {
        return astra_2;
    }

    public Astra getCounterpart(Astra taken) {
        if (astra_1.equals(taken))
            return astra_2;
        if (astra_2.equals(taken))
            return astra_1;
        return null;
    }

    public double getArc() {
        return this.arc;
    }

    public double getOrb() {
        return this.orb;
    }

    public int getUltimateHarmonic() {
        return this.ultimateHarmonic;
    }

    public List<Aspect> getAspects() {
        return this.aspects;
    }
}
