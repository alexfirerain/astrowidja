package ru.swetophor.harmonix;

import ru.swetophor.celestialmechanics.Astra;
import ru.swetophor.celestialmechanics.AstraEntity;
import ru.swetophor.celestialmechanics.ChartObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.CelestialMechanics.calculateStrength;
import static ru.swetophor.celestialmechanics.CelestialMechanics.getArcForHarmonic;
import static ru.swetophor.mainframe.Settings.getPrimalOrb;

/**
 * Олицетворяет группу связанных каким-то резонансом точек
 * (астр из одной или нескольких карт).
 * Точки считаются связанными по данной гармонике, если между ними присутствует
 * соответствующий резонанс; для принадлежности паттерну точка должна иметь
 * резонанс по крайней мере с одной из входящих в него точек.
 */
public class Pattern {
    List<Astra> astras = new ArrayList<>();
    List<PatternElement> entries = new ArrayList<>();
    int harmonic;

    ChartObject heaven; // TODO: категорически ненужный параметр, ибо мобыть синастрией

    double totalClearance = 0.0;

    public Pattern(int harmonic, ChartObject host) {
        this.harmonic = harmonic;
        this.heaven = host;
    }

    public Pattern(int harmonic, List<Astra> astras, ChartObject host) {
        this(harmonic, host);
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
            added.clearanceSum += clearance;
            a.clearanceSum += clearance;
            totalClearance += clearance;
        }
        entries.add(added);
        entries.sort(Comparator
                .comparingDouble(PatternElement::getClearanceSum)
                .thenComparing(pe -> AstraEntity.getAstraEntityNumber(pe.getElement())));
    }

    public List<Astra> getAstrasByConnectivity() {
        return entries.stream()
                .map(PatternElement::getElement)
                .collect(Collectors.toList());
    }

    /**
     * Выдаёт многострочное представление паттерна.
     * Первая строка докладывает среднюю силу связанности и кол-во астр.
     * Каждая следующая строка содержит символ астры и (в скобках) среднюю
     * силу её связанности с другими элементами паттерна.
     *
     * @return многостроку с представлением паттерна.
     */
    public String getConnectivityReport() {
        if (size() == 1)
            return
                    "%c (-)%n"
                            .formatted(entries.get(0).getElement().getSymbol());
        return
                "\t%.0f%% (%d):%n"
                        .formatted(getAverageStrength(), size())
                        +
                        entries.stream()
                                .map(pe -> "\t\t%s (%.0f%%)%n"
                                        .formatted(
                                                pe.getElement().getSymbolWithDegree(),
                                                calculateStrength(
                                                        getPrimalOrb() * size(),
                                                        pe.getClearanceSum() / (size() - 1))))
                                .collect(Collectors.joining());
    }

    /**
     * @return количество астр в паттерне.
     */
    public int size() {
        return astras.size();
    }

    public boolean isEmpty() {
        return astras.isEmpty();
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
                        .mapToDouble(pe -> pe.getClearanceSum() / (size() - 1))
                        .sum() /
                        size());
    }

    public String getString() {
        return entries.stream()
                .map(PatternElement::getElement)
                .map(Astra::getSymbol)
                .map(Object::toString)
                .collect(Collectors.joining());
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
//        if (size() < 2)
//            return false;

        return IntStream.range(0, astras.size() - 1)
                .anyMatch(i -> IntStream.range(i + 1, astras.size())
                        .anyMatch(j ->
                                heaven.resonancePresent(astras.get(i), astras.get(j), harmonic)
                        )
                );
    }

    public List<Astra> getAstras() {
        return this.astras;
    }
    // TODO: сделать считалку паттернов для синастрии

    static class PatternElement {
        private final Astra element;
        private double clearanceSum;

        public PatternElement(Astra astra) {
            element = astra;
        }


        public Astra getElement() {
            return this.element;
        }

        public double getClearanceSum() {
            return this.clearanceSum;
        }
    }

}
