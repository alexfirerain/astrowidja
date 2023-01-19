package ru.swetophor.resogrid;

import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.swetophor.celestialmechanics.CelestialMechanics.calculateStrength;
import static ru.swetophor.celestialmechanics.CelestialMechanics.getArcForHarmonic;
import static ru.swetophor.mainframe.Settings.getPrimalOrb;

public class Pattern {
    List<Astra> astras = new ArrayList<>();
    List<PatternElement> entries = new ArrayList<>();
    int harmonic;

    public Pattern(int harmonic) {
        this.harmonic = harmonic;
    }

    public Pattern(int harmonic, List<Astra> astras) {
        this(harmonic);
        astras.forEach(this::addAstra);
    }

    /**
     * Добавляет астру к паттерну, добавив к сумматору добавляемой
     * зазоры резонанса со всеми уже добавленными, а к сумматорам
     * уже добавленных зазор резонанса с добавляемой.
     *
     * @param astra добавляемая к паттерну астра.
     */
    public void addAstra(Astra astra) {
        astras.add(astra);
        PatternElement added = new PatternElement(astra);
        for (PatternElement a : entries) {
            double clearance = getArcForHarmonic(astra, a.element, harmonic);
            added.totalClearance += clearance;
            a.totalClearance += clearance;
        }
        entries.add(added);
        entries.sort(Comparator
                .comparingDouble(PatternElement::getTotalClearance)
                .thenComparing(pe -> AstraEntity.getAstraEntityNumber(pe.getElement())));
    }

    public List<Astra> getAstrasByConnectivity() {
        return entries.stream()
                .map(PatternElement::getElement)
                .collect(Collectors.toList());
    }

    /**
     * Выдаёт многострочное представление паттерна.
     * Каждая строка содержит символ астры и (в скобках) среднюю
     * силу её связанности с другими элементами паттерна.
     *
     * @return многостроку с представлением паттерна.
     */
    public String getConnectivityReport() {
        if (size() == 1)
            return "%c (-)%n".formatted(entries.get(0).getElement().getSymbol());
        return "%.0f%%@%d%n".formatted(getAverageStrength(), size()) +
                entries.stream()
                        .map(pe -> "\t%c (%.0f%%)%n"
                                .formatted(
                                        pe.getElement().getSymbol(),
                                        calculateStrength(
                                                getPrimalOrb(),
                                                pe.getTotalClearance() / (size() - 1))))
                        .collect(Collectors.joining());
    }

    public List<Astra> getAstras() {
        return astras;
    }

    public int size() {
        return astras.size();
    }

    public void addAllAstras(Pattern pattern) {
        pattern.getAstras()
                .forEach(this::addAstra);
    }

    public double getAverageStrength() {
        if (size() < 2) return 0;

        return calculateStrength(
                getPrimalOrb(),
                entries.stream()
                        .mapToDouble(pe -> pe.getTotalClearance() / size() - 1)
                        .sum() /
                        size());
    }

    /**
     * Предикат, удостоверяющий, что в группе астр наличествует
     * номинальный аспект в явном виде для хотя бы одной пары.
     *
     * @return {@code false}, если паттерн пуст или содержит только
     * одну астру, или если ни в одной из пар элементов нет номинального резонанса.
     * {@code true}, если хотя бы в одной паре номинальный резонанс
     * наличествует.
     */
    public boolean isValid() {
        if (size() < 2)
            return false;
        for (int i = 0; i < astras.size() - 1; i++)
            for (int j = i + 1; j < astras.size(); j++)
                if (astras.get(i)
                        .isInDirectResonanceWith(astras.get(j),
                                harmonic))
                    return true;
        return false;
    }
    // TODO: в паттерне тоже будет ссылка на карту (одиночную или двойную!)

    static class PatternElement {
        private final Astra element;
        private double totalClearance;

        public PatternElement(Astra astra) {
            element = astra;
        }


        public Astra getElement() {
            return this.element;
        }

        public double getTotalClearance() {
            return this.totalClearance;
        }
    }

}
