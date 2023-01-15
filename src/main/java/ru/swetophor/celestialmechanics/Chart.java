package ru.swetophor.celestialmechanics;


import lombok.Setter;
import ru.swetophor.Settings;
import ru.swetophor.resogrid.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static ru.swetophor.celestialmechanics.Mechanics.*;

/**
 * Астрологическое описание момента времени,
 * включающее:<br/>
 *  ♦ уникальный в программе ИД<br/>
 *  ♦ астрологический момент, описанный через:<br/>
     *   • имя, название момента<br/>
     *   • массив астр<br/>
     *   • матрицу резонансов
*/
@Setter
public class Chart extends ChartObject {

    protected List<Astra> astras = new ArrayList<>();

    {
        type = ChartType.COSMOGRAM;
    }
    protected Matrix aspects;

    public Chart(String name) {
        super(name);
    }

    // конструктор

    public static Chart readFromString(String input) {
        String[] lines = input.lines().toArray(String[]::new);
        if (lines.length == 0)
            throw new IllegalArgumentException("текст не содержит строк");

        Chart newChart = new Chart(lines[0]);
        Arrays.stream(lines, 1, lines.length)
                .filter(line -> !line.isBlank()
                        && !line.startsWith("//"))
                .map(Astra::readFromString)
                .forEach(newChart::addAstra);

        return newChart;
    }


    // функциональность

    public void attachAstra(Astra astra) {
        addAstra(astra);
        calculateAspectTable();
    }

    /**
     * Добавляет в карту астру. При этом если с таким именем астра
     * уже присутствует, она обновляется, заменяется.
     * @param astra добавляемая астра.
     */
    public void addAstra(Astra astra) {
        astra.setHeaven(this);
        for (int i = 0; i < astras.size(); i++)
            if (astras.get(i).getName().equals(astra.getName())) {
                astras.set(i, astra);
                return;
            }
        astras.add(astra);
    }

    private void calculateAspectTable() {
        aspects = new Matrix(astras);
    }

    @Override
    public String getAspectTable() {
        calculateAspectTable();
        return getCaption() +
                aspects.resultsOutput();
    }

    public String getCaption() {
        return super.getCaption("%s: %s (№%d)"
                .formatted(type, name, ID));
    }


    @Override
    public String getAstrasList() {
        StringBuilder list = new StringBuilder("%nЗодиакальные позиции (%s):%n".formatted(name));
        astras.forEach(next -> list.append(
                "%s\t %s%n".formatted(
                                next.getNameWithZodiacDegree(),
                                zodiacFormat(next.getZodiacPosition())
                        )
                )
        );
        return list.toString();
    }

    @Override
    public String toString() {
        return "%s (%s, №%d)".formatted(name, type, ID);
    }

    @Override
    public List<Astra> getAstras() {
        return this.astras;
    }


    public Matrix getAspects() {
        return this.aspects;
    }

    public double getAstraPosition(String name) {
        for (Astra a : astras)
            if (a.getName().equals(name))
                return a.getZodiacPosition();
        throw new IllegalArgumentException("Astra " + name + " not found.");
    }

    public static Chart composite(Chart chart_a, Chart chart_b) {
        if (chart_a == null || chart_b == null)
            throw new IllegalArgumentException("карта для композита не найдена");
        Chart composite = new Chart("Средняя карта %s и %s"
                .formatted(chart_a.getName(), chart_b.getName()));

        Astra sun = null, mercury = null, venus = null;
        for (Astra astra : chart_a.getAstras()) {
            Astra counterpart = chart_b.getAstra(astra.getName());
            if (counterpart != null) {
                Astra compositeAstra = new Astra(astra.getName(),
                        findMedian(astra.getZodiacPosition(),
                                counterpart.getZodiacPosition()));
                composite.addAstra(compositeAstra);
                AstraEntity innerBody = AstraEntity.getEntityByName(compositeAstra.getName());
                if (innerBody != null) {
                    switch (innerBody) {
                        case SOL -> sun = compositeAstra;
                        case MER -> mercury = compositeAstra;
                        case VEN -> venus = compositeAstra;
                    }
                }
            }
        }

        if (sun != null) {
            if (mercury != null &&
                    getArc(sun, mercury) > 30.0) {

                mercury.setZodiacPosition(normalizeCoordinate(mercury.getZodiacPosition() + CIRCLE / 2));
                composite.addAstra(mercury);
            }
            if (venus != null &&
                    getArc(sun, venus) > 60.0) {

                venus.setZodiacPosition(normalizeCoordinate(venus.getZodiacPosition() + CIRCLE / 2));
                composite.addAstra(venus);
            }
        }

        return composite;
    }

    /**
     * Отдаёт астру карты по ея имени.
     *
     * @param name имя астры, которую запрашивают.
     * @return астру с таким именем, если она есть в карте, иначе {@code пусто}.
     */
    public Astra getAstra(String name) {
        for (Astra a : astras)
            if (a.getName().equals(name))
                return a;
        return null;
    }

    public List<List<Astra>> findResonanceGroups(int harmonic) {
        List<List<Astra>> patterns = new ArrayList<>();
        boolean[] analyzed = new boolean[astras.size()];
        IntStream.range(0, astras.size())
                .filter(i -> !analyzed[i])
                .mapToObj(i -> analyzeAstra(i, harmonic, analyzed))
                .filter(pattern -> isValidPattern(pattern, harmonic))
                .forEach(patterns::add);
        return patterns;
    }

    private List<Astra> analyzeAstra(int astraIndex, int harmonic, boolean[] analyzed) {
        analyzed[astraIndex] = true;
        List<Astra> currentPattern = new ArrayList<>();
        currentPattern.add(astras.get(astraIndex));
        for (Astra a :
                aspects.getConnectedAstras(astras.get(astraIndex), harmonic)) {
            int i = astras.indexOf(a);
            if (!analyzed[i])
                currentPattern.addAll(analyzeAstra(i, harmonic, analyzed));
        }

        return currentPattern;
    }

    /**
     * Вспомогательный предикат, удостоверяющий, что в группе астр наличествует
     * указанный аспект в явном виде для хотя бы одной пары.
     *
     * @param pattern  анализируемый астропаттерн.
     * @param harmonic число резонанса, который надо удостоверить.
     * @return {@code false}, если паттерна нет, если он пуст или содержит только
     * одну астру, или если ни в одной из пар элементов нет указанного резонанса
     * в явном виде. {@code true}, если хотя бы в одной паре номинальный резонанс
     * наличествует.
     */
    private boolean isValidPattern(List<Astra> pattern, int harmonic) {
        if (pattern == null || pattern.size() < 2)
            return false;
        for (int i = 0; i < pattern.size() - 1; i++)
            for (int j = i + 1; j < pattern.size(); j++)
                if (aspects.astrasInResonance(pattern.get(i), pattern.get(j), harmonic))
                    return true;
        return false;
    }


    public void printResonanceGroupsStupid(int harmonic) {
        List<List<Astra>> groups = findResonanceGroups(harmonic);
        for (List<Astra> pattern : groups) {
            if (!pattern.isEmpty()) {
                System.out.printf("Резонансные группы по числу %d для %s:%n", harmonic, name);
                groups.stream()
                        .filter(group -> !group.isEmpty())
                        .forEach(group -> {
                            group.stream()
                                    .map(Astra::getSymbol)
                                    .forEach(System.out::print);
                            System.out.println();
                        });
                return;
            }
        }
        System.out.printf("Резонансных групп по числу %d для %s не найдено при орбисе 1/%d%n",
                harmonic,
                name,
                Settings.getOrbsDivider());
    }

    private String patternReport(int harmonic) {
        StringBuilder string = new StringBuilder(harmonic + ": ");

        List<List<Astra>> groups = findResonanceGroups(harmonic);

        if (groups.isEmpty())
            return string.append("-").toString();

        groups.stream()
                .filter(group -> !group.isEmpty())
                .forEach(group -> {
                    group.stream()
                            .map(Astra::getSymbol)
                            .forEach(string::append);
                    string.append(" | ");
                });

        return string.toString();
    }

    @Override
    public void printResonanceAnalysis(int upToHarmonic) {
        System.out.printf("Резонансные группы для %s до гармоники %d с исходным орбисом 1/%d%n",
                name,
                upToHarmonic,
                Settings.getOrbsDivider());
        IntStream.rangeClosed(1, upToHarmonic)
                .forEach(h -> System.out.println(patternReport(h)));
    }

}


